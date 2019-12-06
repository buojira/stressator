package org.buojira.stressator.rabbit.consumer;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.service.MessageProducerService;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;
import com.fluig.broker.exception.BrokerException;

public class CallbackConsumer extends StressatorBaseConsumer {

    private final MessageProducerService sender;
    private final BrokerProperties properties;

    public CallbackConsumer(ChannelVO channel,
            String consumerTag,
            MessageProducerService sender,
            BrokerProperties properties) {
        super(channel, consumerTag);
        this.sender = sender;
        this.properties = properties;
    }

    @Override
    public void handleDelivery(BrokerRequestHeader brokerRequestHeader, String register) {
        try {
            System.out.println(" ** Message received: " + register);
            sender.processQueue(properties);
        } catch (BrokerException e) {
            e.printStackTrace();
        }
    }

}
