package org.buojira.rabbit;

import java.net.UnknownHostException;

import org.assertj.core.api.Assertions;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.MessageRepository;
import org.buojira.stressator.rabbit.runner.Worker;
import org.buojira.stressator.rabbit.service.BrokerOverloadService;
import org.buojira.stressator.rabbit.service.ParallelOverloadService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fluig.broker.exception.BrokerException;

public class MessageWarrantArrivalIT extends StressatorBaseIT {

    @Autowired
    private BrokerOverloadService target;

    @Autowired
    private ParallelOverloadService overloader;

    @Test
    public void overloadTest() throws InterruptedException {

        BrokerProperties props = getQAFluigIO();
        Number duration = 0.1;

        overloader.overloadRabbit(duration, props, "A");
    }

    @Test
    public void controlPayloadTest() throws InterruptedException, BrokerException, UnknownHostException {
        target.sendAndControlPayload(getQAFluigIO(), 10);
        Assertions.assertThat(true).isTrue();
    }

    @Test
    public void sendOneAtATime() throws InterruptedException {

        BrokerProperties props = getQAFluigIO();
        Number duration = 0.1;

        Worker sender = target.sendAndWait(props, duration);

        Number waitingTime = (duration.floatValue() * 1000) + 5000;
        Thread.sleep(waitingTime.longValue());

        Worker cleaner = target.clearQueues(props);

        while (!sender.isFinished() && !cleaner.isFinished()) {
            Thread.sleep(5000l);
        }

        Assertions.assertThat(MessageRepository.getInstance().isRabbitMessageQueueEmpty()).isTrue();
    }

    @Test
    public void justListenUp() throws InterruptedException {
        target.startUpListeners(getQAFluigIO());
        Thread.sleep(60 * 1000l);
    }

    @Test
    public void justSend() throws BrokerException {
        target.send(getQAFluigIO(), "bla", 226);
    }

}
