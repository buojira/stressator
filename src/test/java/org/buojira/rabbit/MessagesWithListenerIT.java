package org.buojira.rabbit;

import org.buojira.stressator.rabbit.service.MessageConsumerService;
import org.buojira.stressator.rabbit.service.MessageProducerService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fluig.broker.exception.BrokerException;

public class MessagesWithListenerIT extends StressatorBaseIT {

    @Autowired
    private MessageProducerService producer;

    @Autowired
    private MessageConsumerService consumer;

    @Before
    public void before() {
        consumer.startupProcessingListener(getQAFluigIO());
    }

    @Test
    public void drill() throws BrokerException {
        producer.sendSomething(getQAFluigIO(), "Blá");
    }

}
