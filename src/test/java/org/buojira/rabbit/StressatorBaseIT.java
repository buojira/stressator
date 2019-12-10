package org.buojira.rabbit;

import org.buojira.stressator.StressatorApplication;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StressatorApplication.class)
@Ignore
public class StressatorBaseIT {

    protected BrokerProperties getQAFluigIO() {
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
