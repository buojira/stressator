package org.buojira.stressator.rabbit.consumer;

import org.buojira.stressator.file.RabbitMessage;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.service.MessageProducerService;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;
import com.fluig.broker.exception.BrokerException;

public class RabbitMessageCallbackConsumer extends RabbitMessageConsumer {

    private final MessageProducerService sender;
    private final BrokerProperties properties;

    public RabbitMessageCallbackConsumer(ChannelVO channel,
            String consumerTag,
            MessageProducerService sender,
            BrokerProperties properties) {
        super(channel, consumerTag);
        this.sender = sender;
        this.properties = properties;
    }

    @Override
    public void handleDelivery(BrokerRequestHeader brokerRequestHeader,
            RabbitMessage vo) throws BrokerException {
        System.out.println("RabbitMessageCallbackConsumer - ID: " + vo.getId());
        sender.processRabbitQueue(properties);
    }

}
