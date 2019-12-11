package org.buojira.stressator.rabbit.runner;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.service.MessageConsumerService;
import org.buojira.stressator.rabbit.service.MessageProducerService;

public class ManyConnectionsWorker extends Worker {

    private final BrokerProperties brokerProperties;
    private final int index;
    private final MessageProducerService producerService;
    private final MessageConsumerService consumerService;

    public ManyConnectionsWorker(
            BrokerProperties brokerProperties,
            int index,
            MessageProducerService producerService,
            MessageConsumerService consumerService) {
        this.brokerProperties = brokerProperties;
        this.index = index;
        this.producerService = producerService;
        this.consumerService = consumerService;
    }

    @Override
    protected void work() throws Exception {
        consumerService.startupProcessingListener(brokerProperties, true);
        while (true) {
            producerService.sendSomething(brokerProperties, "message number " + index, true);
            Thread.sleep(10000l);
        }
    }

}
