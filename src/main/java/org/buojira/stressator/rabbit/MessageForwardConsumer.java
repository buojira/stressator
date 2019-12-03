package org.buojira.stressator.rabbit;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;
import com.fluig.broker.exception.BrokerException;

public class MessageForwardConsumer extends MessageConsumer {

    private MessageProducerService producerService;
    private boolean looseIt = true;

    public MessageForwardConsumer(ChannelVO channel,
            String consumerTag,
            MessageConsumerService service,
            MessageProducerService producerService) {
        super(channel, consumerTag, service);
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
                    producerService.notifyArrival(register);
                }
            } else {
                producerService.notifyArrival(register);
            }
        } catch (BrokerException e) {
            e.printStackTrace();
        }
        wait4It(2000);
    }

}
