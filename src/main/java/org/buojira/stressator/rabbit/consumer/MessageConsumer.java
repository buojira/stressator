package org.buojira.stressator.rabbit.consumer;

import org.buojira.stressator.rabbit.service.MessageConsumerService;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;

public class MessageConsumer extends StressatorBaseConsumer {

    private final MessageConsumerService service;

    public MessageConsumer(ChannelVO channel, String consumerTag, MessageConsumerService service) {
        super(channel, consumerTag);
        this.service = service;
    }

    @Override
    public void handleDelivery(BrokerRequestHeader brokerRequestHeader, String register) {
        String[] split = register.split("\\|");
        countMessage(split[0]);
        if (getTotalAmount() % 10000 == 0) {
            System.out.println(register);
        }
    }

    protected int getTotalAmount() {
        return service.getTotalAmount();
    }

    protected void countMessage(String host) {
        service.countMessage(host);
    }

}
