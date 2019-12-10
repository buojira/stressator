package org.buojira.stressator.rabbit;

import java.util.Map;
import java.util.TreeMap;

import com.fluig.broker.BrokerClient;
import com.fluig.broker.controller.ChannelController;
import com.fluig.broker.domain.ChannelControllerDTO;
import com.fluig.broker.domain.builder.ChannelControllerDTOBuilder;
import com.fluig.broker.domain.builder.ConnectionVOBuilder;
import com.fluig.broker.exception.BrokerException;

public class RabbitMQBrokerClient {

    private static RabbitMQBrokerClient instance;

    private final Map<String, ChannelController> processingChannels;
    private final Map<String, ChannelController> statusChannels;
    private BrokerClient brokerClient;

    private RabbitMQBrokerClient() {
        statusChannels = new TreeMap<>();
        processingChannels = new TreeMap<>();
    }

    public static RabbitMQBrokerClient getInstance() {
        if (instance == null) {
            instance = new RabbitMQBrokerClient();
        }
        return instance;
    }

    public ChannelController getStatusChannel(BrokerProperties properties) throws BrokerException {
        String key = getKey(properties);
        ChannelController result = statusChannels.get(key);
        if (result == null) {
            ChannelControllerDTO dto = ChannelControllerDTOBuilder.of()
                    .exchangeName(properties.getExchangeName())
                    .routingKey(properties.getBrokerStatusQueue())
                    .queueName(properties.getBrokerStatusQueue())
                    .build();
            result = getBrokerClient(properties)
                    .createDirectChannel(dto);
            statusChannels.put(key, result);
            System.out.println(" ");
            System.out.println(" **************** ");
            System.out.println(" **************** Callback ");
            System.out.println(" ");
            System.out.println("     Exchange: " + properties.getExchangeName());
            System.out.println("        Queue: " + properties.getQueueName());
            System.out.println(" ");
            System.out.println(" **************** ");
            System.out.println(" **************** ");
            System.out.println(" ");
        }
        return result;
    }

    public ChannelController getProcessingChannel(BrokerProperties properties) throws BrokerException {
        return getProcessingChannel(properties, false);
    }

    public ChannelController getProcessingChannel(BrokerProperties properties, boolean forceNewConnection) throws BrokerException {

        String key = getKey(properties);
        ChannelController result = processingChannels.get(key);

        if (result == null) {

            ChannelControllerDTO dto = ChannelControllerDTOBuilder.of()
                    .exchangeName(properties.getExchangeName())
                    .routingKey(properties.getQueueName())
                    .queueName(properties.getQueueName())
                    .callbackQueueName(properties.getBrokerStatusQueue())
                    .build();

            result = getBrokerClient(properties, forceNewConnection)
                    .createDirectChannel(dto);

            processingChannels.put(key, result);

            System.out.println(" ");
            System.out.println(" **************** ");
            System.out.println(" **************** Processing ");
            System.out.println(" ");
            System.out.println("     Exchange: " + properties.getExchangeName());
            System.out.println("        Queue: " + properties.getQueueName());
            System.out.println(" Status Queue: " + properties.getBrokerStatusQueue());
            System.out.println(" ");
            System.out.println(" **************** ");
            System.out.println(" **************** ");
            System.out.println(" ");

        }
        return result;
    }

    public BrokerClient getBrokerClient(BrokerProperties properties) {
        return getBrokerClient(properties, false);
    }

    public BrokerClient getBrokerClient(BrokerProperties properties, boolean forceNewConnection) {

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
                            .alwaysUseNewConnection(forceNewConnection)
                            .build()
            );

        }

        return brokerClient;

    }

    private String getKey(BrokerProperties props) {
        return props.getExchangeName() + props.getQueueName();
    }

}
