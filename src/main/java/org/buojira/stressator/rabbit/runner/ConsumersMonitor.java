package org.buojira.stressator.rabbit.runner;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.service.MessageConsumerService;
import org.buojira.stressator.rabbit.service.MessageProducerService;

public class ConsumersMonitor implements Runnable {

    private final MessageProducerService producerService;
    private final MessageConsumerService consumerService;
    private final BrokerProperties properties;

    public ConsumersMonitor(MessageProducerService producerService,
            MessageConsumerService consumerService,
            BrokerProperties properties) {

        this.producerService = producerService;
        this.consumerService = consumerService;
        this.properties = properties;

    }

    @Override
    public void run() {
        if (properties != null) {
            consumerService.startupSecureProcessingListener(producerService, properties);
            consumerService.startupStatusListener(properties);
        } else {
            consumerService.startupSecureProcessingListener(producerService);
            consumerService.startupStatusListener();
        }
        new Thread(new ArrivalMonitor(producerService)).start();
    }

}
