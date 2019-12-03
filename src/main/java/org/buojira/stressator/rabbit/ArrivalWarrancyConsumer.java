package org.buojira.stressator.rabbit;

import java.util.Map;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;

public class ArrivalWarrancyConsumer extends MessageConsumer {

    private final Map<String, Number> codingMap;

    public ArrivalWarrancyConsumer(ChannelVO channel, String consumerTag, MessageConsumerService service,
            Map<String, Number> codingMap) {
        super(channel, consumerTag, service);
        this.codingMap = codingMap;
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
        wait4It(2000);
    }
}
