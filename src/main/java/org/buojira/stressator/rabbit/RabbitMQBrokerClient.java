package org.buojira.stressator.rabbit;

import com.fluig.broker.BrokerClient;
import com.fluig.broker.controller.ChannelController;
import com.fluig.broker.domain.ChannelControllerDTO;
import com.fluig.broker.domain.builder.ChannelControllerDTOBuilder;
import com.fluig.broker.domain.builder.ConnectionVOBuilder;
import com.fluig.broker.exception.BrokerException;

public class RabbitMQBrokerClient {

    private final BrokerProperties properties;
    private ChannelController channelController;
    private BrokerClient brokerClient;

    public RabbitMQBrokerClient(BrokerProperties properties) {
        this.properties = properties;
    }

    public ChannelController getChannel() throws BrokerException {

        if (channelController == null) {

            ChannelControllerDTO dto = ChannelControllerDTOBuilder.of()
                    .exchangeName(properties.getExchangeName())
                    .routingKey(properties.getQueueName())
                    .queueName(properties.getQueueName())
                    .build();

            channelController = getBrokerClient()
                    .createFanoutChannel(dto);

        }

        return channelController;
    }

    public BrokerClient getBrokerClient() {

        if (brokerClient == null) {

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
