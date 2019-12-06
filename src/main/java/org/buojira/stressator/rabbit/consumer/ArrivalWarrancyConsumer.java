package org.buojira.stressator.rabbit.consumer;

import java.util.Map;

import org.buojira.stressator.rabbit.MessageRepository;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;

public class ArrivalWarrancyConsumer extends StressatorBaseConsumer {

    private final Map<String, Number> codingMap;

    public ArrivalWarrancyConsumer(ChannelVO channel, String consumerTag) {
        super(channel, consumerTag);
        this.codingMap = MessageRepository.getInstance().getQueueAgeMap();
    }

    @Override
    public void handleDelivery(BrokerRequestHeader brokerRequestHeader, String register) {
        System.out.println("StatusConsumer received: " + register);
        if (!codingMap.containsKey(register)) {
            System.out.println("+-- NOT found yet");
        } else {
            codingMap.remove(register);
            System.out.println("+-- removed from map");
        }
    }

}
