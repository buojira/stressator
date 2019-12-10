package org.buojira.stressator.rabbit.consumer;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.service.MessageConsumerService;
import org.buojira.stressator.rabbit.service.MessageProducerService;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;
import com.fluig.broker.exception.BrokerException;

public class MessageForwardConsumer extends MessageConsumer {

    private MessageProducerService producerService;
    private final BrokerProperties props;
    private boolean looseIt = false;

    public MessageForwardConsumer(ChannelVO channel,
            BrokerProperties props,
            MessageConsumerService service,
            MessageProducerService producerService) {
        super(channel, props.getTags(), service);
        this.props = props;
        this.producerService = producerService;
    }

    @Override
    public void handleDelivery(BrokerRequestHeader brokerRequestHeader, String register) {
        String[] split = register.split("\\|");
        countMessage(split[0]);
        System.out.println("MessageForwardConsumer received:" + register);
        try {
            if (Long.valueOf(split[1]) % 20 == 0) {
                if (looseIt) {
                    looseIt = false;
                } else {
                    producerService.notifyArrival(props, register);
                }
            } else {
                producerService.notifyArrival(props, register);
            }
        } catch (BrokerException e) {
            e.printStackTrace();
        }
//        wait4It(2000);
    }

}
