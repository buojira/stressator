package org.buojira.stressator.rabbit.service;

import java.util.Map;
import java.util.TreeMap;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.RabbitMQBrokerClient;
import org.buojira.stressator.rabbit.consumer.ArrivalWarrancyConsumer;
import org.buojira.stressator.rabbit.consumer.MessageConsumer;
import org.buojira.stressator.rabbit.consumer.MessageForwardConsumer;
import org.buojira.stressator.rabbit.consumer.StressatorBaseConsumer;
import org.springframework.stereotype.Service;

import com.fluig.broker.controller.ChannelController;
import com.fluig.broker.exception.BrokerException;

@Service
public class MessageConsumerService {

    private RabbitMQBrokerClient client;
    private final BrokerProperties properties;
    private int totalAmmount;
    private Map<String, Long> counterMap;

    public MessageConsumerService(BrokerProperties properties) {
        counterMap = new TreeMap<>();
        this.properties = properties;
    }

    public void startupProcessingListener() {
        startupProcessingListener(properties);
    }

    public void startupProcessingListener(BrokerProperties props) {
        try {

            totalAmmount = 0;
            ChannelController channelController = getClient(props).getProcessingChannel();
            MessageConsumer consumer = new MessageConsumer(
                    channelController.getChannel(),
                    properties.getTags(),
                    this
            );
            channelController.addListener(consumer);

            System.out.println("I am listening and processing... ");

        } catch (BrokerException e) {
            e.printStackTrace();
        }
    }

    public void startupSecureProcessingListener(MessageProducerService producerService) {
        startupSecureProcessingListener(producerService, properties);
    }

    public void startupSecureProcessingListener(MessageProducerService producerService, BrokerProperties props) {
        try {

            totalAmmount = 0;
            ChannelController channelController = getClient(props).getProcessingChannel();
            MessageConsumer consumer = new MessageForwardConsumer(
                    channelController.getChannel(),
                    properties.getTags(),
                    this,
                    producerService
            );
            channelController.addListener(consumer);

            System.out.println("I am listening and processing, and sending arrival notice... ");

        } catch (BrokerException e) {
            e.printStackTrace();
        }
    }

    public void startupStatusListener() {
        startupStatusListener(properties);
    }

    public void startupStatusListener(BrokerProperties props) {
        try {

            ChannelController channelController = getClient(props).getStatusChannel();
            StressatorBaseConsumer consumer = new ArrivalWarrancyConsumer(
                    channelController.getChannel(),
                    properties.getTags()
            );
            channelController.addListener(consumer);

            System.out.println("I am listening and monitoring... ");

        } catch (BrokerException e) {
            e.printStackTrace();
        }
    }

    public void countMessage(String host) {
        totalAmmount++;
        Long value = counterMap.get(host);
        if (value != null) {
            counterMap.put(host, value.longValue() + 1L);
        } else {
            counterMap.put(host, 1L);
        }
    }

    public int getTotalAmmount() {
        return totalAmmount;
    }

    public Map<String, Long> getCounterMap() {
        return counterMap;
    }

    private RabbitMQBrokerClient getClient(BrokerProperties props) {
        if (client == null) {
            client = new RabbitMQBrokerClient(props);
        }
        return client;
    }
}
