package org.buojira.rabbit;

import java.net.UnknownHostException;

import org.assertj.core.api.Assertions;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.MessageRepository;
import org.buojira.stressator.rabbit.runner.QueueCleaner;
import org.buojira.stressator.rabbit.runner.Worker;
import org.buojira.stressator.rabbit.service.BrokerOverloadService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fluig.broker.exception.BrokerException;

public class MessageWarrantArrivalIT extends StressatorBaseIT {

    @Autowired
    private BrokerOverloadService target;

    @Test
    public void controlPayloadTest() throws InterruptedException, BrokerException, UnknownHostException {
        target.sendAndControlPayload(getQAFluigIO(),10);
        Assertions.assertThat(true).isTrue();
    }

    @Test
    public void sendOneAtATime() throws InterruptedException {
        BrokerProperties props = getQAFluigIO();

        Worker sender = target.sendAndWait(props, 0.1);

        while (!sender.isFinished()) {
            Thread.sleep(5000l);
        }

        Worker cleaner = target.clearQueues(props);

        while (!cleaner.isFinished()) {
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
        target.send(getQAFluigIO(),"bla", 226);
    }

    private BrokerProperties getQAFluigIO() {
        BrokerProperties props = new BrokerProperties();
        props.setBrokerHost("localhost");
        props.setBrokerPort("5672");
        props.setBrokerUserName("guest");
        props.setBrokerPassword("guest");
        props.setTags("stress_test");
        props.setExchangeName("AAAEXCHANGETEST_0001");
        props.setQueueName("AAAQUEUETEST_101001");
        props.setBrokerStatusQueue("AAA-STATUSQI.V1");
        props.setVirtualHost("/");
        return props;
    }

}
