package org.buojira.stressator.rabbit;

import org.springframework.stereotype.Service;

import com.fluig.broker.controller.ChannelController;
import com.fluig.broker.exception.BrokerException;

@Service
public class MessageConsumerService {

    private final RabbitMQBrokerClient client;
    private final BrokerProperties properties;
    private String host;
    private int messageNumber;
    private int messageAmmount;

    public MessageConsumerService(BrokerProperties properties) {
        this.properties = properties;
        this.client = new RabbitMQBrokerClient(properties);
    }

    public void startupListener() {
        try {
            messageAmmount = 0;
            ChannelController channelController = client.getChannel();
            MessageConsumer consumer = new MessageConsumer(
                    channelController.getChannel(),
                    properties.getTags(),
                    this
            );
            channelController.addListener(consumer);
            System.out.println("I am listening ... ");
        } catch (BrokerException e) {
            e.printStackTrace();
        }
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public void countMessage() {
        messageAmmount++;
    }

    public int getMessageAmmount() {
        return messageAmmount;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
