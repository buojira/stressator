package org.buojira.stressator.rabbit;

import org.springframework.stereotype.Service;

import com.fluig.broker.domain.BrokerRequest;
import com.fluig.broker.domain.builder.BrokerRequestBuilder;
import com.fluig.broker.domain.builder.BrokerRequestHeaderBuilder;
import com.fluig.broker.exception.BrokerException;

@Service
public class MessageProducerService {

    private final RabbitMQBrokerClient client;
    private final BrokerProperties properties;

    public MessageProducerService(BrokerProperties properties) {
        this.properties = properties;
        this.client = new RabbitMQBrokerClient(properties);
    }

    public void sendSomething(String message) throws BrokerException {
        client.getChannel().sendMessage(createRequest(message));
    }

    private BrokerRequest createRequest(String message) {
        return BrokerRequestBuilder.of()
                .header(BrokerRequestHeaderBuilder.of()
                        .build())
                .body(message.getBytes())
                .build();
    }

}
