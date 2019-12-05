package org.buojira.stressator.rabbit;

import com.fluig.broker.BrokerClient;
import com.fluig.broker.controller.ChannelController;
import com.fluig.broker.domain.ChannelControllerDTO;
import com.fluig.broker.domain.builder.ChannelControllerDTOBuilder;
import com.fluig.broker.domain.builder.ConnectionVOBuilder;
import com.fluig.broker.exception.BrokerException;

public class RabbitMQBrokerClient {

    private final BrokerProperties properties;
    private ChannelController processingChannel;
    private ChannelController statusChannel;
    private BrokerClient brokerClient;

    public RabbitMQBrokerClient(BrokerProperties properties) {
        this.properties = properties;
    }

    public ChannelController getStatusChannel() throws BrokerException {
        if (statusChannel == null) {

            ChannelControllerDTO dto = ChannelControllerDTOBuilder.of()
                    .exchangeName(properties.getExchangeName())
                    .routingKey(properties.getBrokerStatusQueue())
                    .queueName(properties.getBrokerStatusQueue())
                    .build();

            statusChannel = getBrokerClient()
                    .createDirectChannel(dto);

        }
        return statusChannel;
    }

    public ChannelController getProcessingChannel() throws BrokerException {

        if (processingChannel == null) {

            ChannelControllerDTO dto = ChannelControllerDTOBuilder.of()
                    .exchangeName(properties.getExchangeName())
                    .routingKey(properties.getQueueName())
                    .queueName(properties.getQueueName())
                    .callbackQueueName(properties.getBrokerStatusQueue())
                    .build();

            processingChannel = getBrokerClient()
                    .createDirectChannel(dto);

        }

        return processingChannel;

    }

    public BrokerClient getBrokerClient() {

        if (brokerClient == null) {

            System.out.println(" ");
            System.out.println(" **************** ");
            System.out.println(" **************** ");
            System.out.println(" ");
            System.out.println(" Connecting Rabbit ... ");
            System.out.println("         Host: " + properties.getBrokerHost());
            System.out.println("         Port: " + properties.getBrokerPort());
            System.out.println("        Login: " + properties.getBrokerUserName());
            System.out.println("     Password: " + properties.getBrokerPassword());
            System.out.println("     Exchange: " + properties.getExchangeName());
            System.out.println("        Queue: " + properties.getQueueName());
            System.out.println(" Status Queue: " + properties.getBrokerStatusQueue());
            System.out.println(" ");
            System.out.println(" **************** ");
            System.out.println(" **************** ");
            System.out.println(" ");

            brokerClient = new BrokerClient(
                    ConnectionVOBuilder.of()
                            .host(properties.getBrokerHost())
                            .virtualHost(properties.getVirtualHost())
                            .port(Integer.valueOf(properties.getBrokerPort()))
                            .userName(properties.getBrokerUserName())
                            .password(properties.getBrokerPassword())
                            .build()
            );

        }

        return brokerClient;

    }

}
