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

    private QueueConsumer callbackConsumer;

    public MessageProducerService() {
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
            getClient().getProcessingChannel(props)
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
            getClient().getProcessingChannel(props)
                    .sendMessage(
                            createRequest(msg),
                            getCallbackConsumer(props)
                    );
        }
    }

    public void sendSomething(BrokerProperties props, String message, boolean forceNewConnection)
            throws BrokerException {

        BrokerRequest request = createRequest(message);

        getClient()
                .getProcessingChannel(
                        props,
                        forceNewConnection
                )
                .sendMessage(request);

    }

    public void sendSomething(BrokerProperties props, String message) throws BrokerException {
        sendSomething(
                props,
                message,
                false);
    }

    public void notifyArrival(BrokerProperties props, String register) throws BrokerException {
        getClient().getStatusChannel(props).sendMessage(createRequest(register));
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

    private RabbitMQBrokerClient getClient() {
        return RabbitMQBrokerClient.getInstance();
    }

    public QueueConsumer getCallbackConsumer(BrokerProperties props) throws BrokerException {
//        if (callbackConsumer == null) {
            callbackConsumer = new RabbitMessageCallbackConsumer(
                    getClient().getProcessingChannel(props).getChannel(),
                    props.getTags(),
                    this,
                    props
            );
//        }
//        callbackConsumer.handleConsumeOk(props.getTags());
        return callbackConsumer;
    }

}
