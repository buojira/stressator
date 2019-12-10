package org.buojira.rabbit;

import org.buojira.stressator.rabbit.service.MessageConsumerService;
import org.buojira.stressator.rabbit.service.MessageProducerService;
import org.buojira.stressator.rabbit.service.ParallelOverloadService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fluig.broker.exception.BrokerException;

public class MessagesWithListenerIT extends StressatorBaseIT {

    @Autowired
    private MessageProducerService producer;

    @Autowired
    private MessageConsumerService consumer;

    @Autowired
    private ParallelOverloadService parallelOverloadService;

    @Test
    public void drill() throws BrokerException {
        consumer.startupProcessingListener(getQAFluigIO());
        producer.sendSomething(getQAFluigIO(), "Blá");
    }

    @Test
    public void testMultiConnections() throws InterruptedException {
        parallelOverloadService.alwaysUseNewConnectionAndKeepItOpened(
                getQAFluigIO(),
                2);
    }

}
