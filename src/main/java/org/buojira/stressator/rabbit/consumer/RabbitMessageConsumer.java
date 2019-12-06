package org.buojira.stressator.rabbit.consumer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.buojira.stressator.file.RabbitMessage;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;
import com.fluig.broker.exception.BrokerException;
import com.fluig.broker.rabbit.RabbitMQAbstractConsumer;
import com.fluig.identity.utils.JsonConverter;

public class RabbitMessageConsumer extends RabbitMQAbstractConsumer<RabbitMessage> {

    public RabbitMessageConsumer(ChannelVO channel, String consumerTag) {
        super(channel, consumerTag);
    }

    @Override
    protected RabbitMessage getBody(byte[] bytes) throws IOException {
        String content = new String(bytes, StandardCharsets.UTF_8);
        return JsonConverter.fromJson(content, RabbitMessage.class);
    }

    @Override
    public void handleDelivery(BrokerRequestHeader brokerRequestHeader,
            RabbitMessage vo) throws BrokerException {
        System.out.println("ID: " + vo.getId());
        System.out.println("Content: " + vo.getContent());
    }

}
