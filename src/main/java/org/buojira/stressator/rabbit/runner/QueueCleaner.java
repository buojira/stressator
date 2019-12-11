package org.buojira.stressator.rabbit.runner;

import java.text.NumberFormat;

import org.buojira.stressator.Formatter;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.service.MessageConsumerService;

public class QueueCleaner extends Worker {

    private final MessageConsumerService consumerService;
    private final BrokerProperties props;

    private Number received;

    public QueueCleaner(MessageConsumerService consumerService,
            BrokerProperties props) {
        this.props = props;
        this.consumerService = consumerService;
    }

    @Override
    protected void work() throws Exception {

        NumberFormat formatter = Formatter.DECIMAL_FORMAT;
        consumerService.startupProcessingListener(props);

        int partial1;
        int partial2 = 0;
        received = 0;

        while (received.intValue() == 0) {
            partial1 = consumerService.getTotalAmount();

            System.out.println(props.getExchangeName() +
                    " - p1:"
                    + formatter.format(partial1)
                    + " | p2:"
                    + formatter.format(partial2));

            if (partial2 > 0 && partial1 == partial2) {
                received = consumerService.getTotalAmount();
            } else {
                partial2 = partial1;
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public Number getReceived() {
        return received;
    }

}
