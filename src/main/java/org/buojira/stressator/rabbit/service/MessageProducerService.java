package org.buojira.stressator.rabbit.service;

import java.util.Queue;

import org.buojira.stressator.file.RabbitMessage;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.MessageRepository;
import org.buojira.stressator.rabbit.RabbitMQBrokerClient;
import org.buojira.stressator.rabbit.consumer.RabbitMessageCallbackConsumer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fluig.broker.domain.BrokerRequest;
import com.fluig.broker.domain.builder.BrokerRequestBuilder;
import com.fluig.broker.domain.builder.BrokerRequestHeaderBuilder;
import com.fluig.broker.exception.BrokerException;
import com.fluig.broker.rabbit.QueueConsumer;
import com.fluig.identity.utils.JsonConverter;

@Service
public class MessageProducerService {

    private final BrokerProperties properties;
    private RabbitMQBrokerClient client;
    private QueueConsumer callbackConsumer;

    public MessageProducerService(BrokerProperties properties) {
        this.properties = properties;
    }

    public void queueMessage(BrokerProperties props,
            String message) throws BrokerException {
        MessageRepository.getInstance().getMessageCache().offer(message);
    }

    public void processQueue(BrokerProperties props) throws BrokerException {
        Queue<String> cache = MessageRepository.getInstance().getMessageCache();
        if (!cache.isEmpty()) {
            String key = cache.poll();
            props.setTags(key);
            getClient(props).getProcessingChannel()
                    .sendMessage(
                            createRequest(key),
                            getCallbackConsumer(props)
                    );
        }
    }

    public void processRabbitQueue(BrokerProperties props) throws BrokerException {
        Queue<RabbitMessage> cache = MessageRepository.getInstance().getRabbitMessagesCache();
        if (!cache.isEmpty()) {
            RabbitMessage msg = cache.poll();
            props.setTags(msg.getId());
            getClient(props).getProcessingChannel()
                    .sendMessage(
                            createRequest(msg),
                            getCallbackConsumer(props)
                    );
        }
    }

    public void sendSomething(BrokerProperties props, String message) throws BrokerException {
        getClient(props).getProcessingChannel().sendMessage(createRequest(message));
    }

    public void sendSomething(String message) throws BrokerException {
        sendSomething(properties, message);
    }

    public void notifyArrival(BrokerProperties props, String register) throws BrokerException {
        getClient(props).getStatusChannel().sendMessage(createRequest(register));
    }

    public void notifyArrival(String register) throws BrokerException {
        notifyArrival(properties, register);
    }

    private BrokerRequest createRequest(String message) {
        return BrokerRequestBuilder.of()
                .header(BrokerRequestHeaderBuilder.of()
                        .build())
                .body(message.getBytes())
                .build();
    }

    private BrokerRequest createRequest(RabbitMessage message) throws BrokerException {
        try {
            return BrokerRequestBuilder.of()
                    .header(BrokerRequestHeaderBuilder.of()
                            .build())
                    .body(JsonConverter.toJson(message).getBytes())
                    .build();
        } catch (JsonProcessingException e) {
            throw new BrokerException(e);
        }
    }

    private RabbitMQBrokerClient getClient(BrokerProperties props) {
        if (client == null) {
            client = new RabbitMQBrokerClient(props);
        }
        return client;
    }

    public QueueConsumer getCallbackConsumer(BrokerProperties props) throws BrokerException {
        if (callbackConsumer == null) {
            callbackConsumer = new RabbitMessageCallbackConsumer(
                    getClient(props).getProcessingChannel().getChannel(),
                    props.getTags(),
                    this,
                    props
            );
        }
        callbackConsumer.handleConsumeOk(props.getTags());
        return callbackConsumer;
    }

}
