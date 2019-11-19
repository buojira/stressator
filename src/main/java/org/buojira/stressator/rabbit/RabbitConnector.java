package org.buojira.stressator.rabbit;

import org.springframework.stereotype.Service;

import com.fluig.broker.BrokerClient;
import com.fluig.broker.controller.ChannelController;
import com.fluig.broker.domain.ChannelControllerDTO;
import com.fluig.broker.domain.builder.ChannelControllerDTOBuilder;
import com.fluig.broker.domain.builder.ConnectionVOBuilder;
import com.fluig.broker.exception.BrokerException;

@Service
public class RabbitConnector {
    private final BrokerClient client;
    private final BrokerProperties properties;

    public RabbitConnector(BrokerProperties properties) {
        this.properties = properties;
        client = connect(properties);
        startupListener();
    }

    private BrokerClient connect(BrokerProperties properties) {
        return new BrokerClient(ConnectionVOBuilder.of()
                .host(properties.getBrokerHost())
                .virtualHost(properties.getVirtualHost())
                .port(Integer.valueOf(properties.getBrokerPort()))
                .userName(properties.getBrokerUserName())
                .password(properties.getBrokerPassword())
                .build());
    }

    private void startupListener() {

        try {

            ChannelControllerDTO dto = ChannelControllerDTOBuilder.of()
                    .exchangeName(properties.getExchangeName())
                    .routingKey(properties.getQueueName())
                    .queueName(properties.getQueueName())
                    .build();

            System.out.println("DataConnection: " + dto);

            ChannelController controller = client.createFanoutChannel(dto);

            System.out.println("Fanout Channel Opened");

            controller.addListener(
                    new RabbitListener(
                            controller.getChannel(),
                            properties.getTags()
                    )
            );
            System.out.println("I am listening ... ");

        } catch (BrokerException e) {
            e.printStackTrace();
        }

    }

}
