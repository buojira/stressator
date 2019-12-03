package org.buojira.stressator.rabbit;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.fluig.broker.controller.ChannelController;
import com.fluig.broker.exception.BrokerException;

@Service
public class MessageConsumerService {

    private final RabbitMQBrokerClient client;
    private final BrokerProperties properties;
    private int totalAmmount;
    private Map<String, Long> counterMap;

    public MessageConsumerService(BrokerProperties properties) {
        counterMap = new TreeMap<>();
        this.properties = properties;
        this.client = new RabbitMQBrokerClient(properties);
    }

    public void startupProcessingListener() {
        try {

            totalAmmount = 0;
            ChannelController channelController = client.getProcessingChannel();
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
        try {

            totalAmmount = 0;
            ChannelController channelController = client.getProcessingChannel();
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

    public void startupStatusListener(Map<String, Number> codingMap) {
        try {

            ChannelController channelController = client.getStatusChannel();
            MessageConsumer consumer = new ArrivalWarrancyConsumer(
                    channelController.getChannel(),
                    properties.getTags(),
                    this,
                    codingMap
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

}
