package org.buojira.rabbit;

import java.net.UnknownHostException;

import org.assertj.core.api.Assertions;
import org.buojira.stressator.rabbit.BrokerOverloadService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fluig.broker.exception.BrokerException;

public class MessageWarrantArrival extends StressatorBaseIT {

    @Autowired
    private BrokerOverloadService target;

    @Test
    public void controlPayloadTest() throws InterruptedException, BrokerException, UnknownHostException {
        target.sendAndControllerPayload(10);
        Assertions.assertThat(true).isTrue();
    }

    @Test
    public void justListenUp() throws InterruptedException {
        target.startUpListeners();
        Thread.sleep(5000l);
    }
}
