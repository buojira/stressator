package org.buojira.rabbit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import org.buojira.stressator.rabbit.MessageConsumerService;
import org.buojira.stressator.rabbit.MessageProducerService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fluig.broker.exception.BrokerException;

public class OverloadingTestIT extends StressatorBaseIT {

    @Autowired
    private MessageProducerService producerService;

    @Autowired
    private MessageConsumerService consumerService;

    private final Number totalOfSentMessages = 1000;
//    private final int duration = 1644;

    @Test
    public void ddsThresholdTest() throws BrokerException, UnknownHostException {
        long begining = Calendar.getInstance().getTimeInMillis();
        String hostName = InetAddress.getLocalHost().getHostName();
        for (int i = 1; i <= totalOfSentMessages.longValue(); i++) {
            if (i % 5000 == 0) {
                System.out.println(i);
            }
            producerService.sendSomething(hostName + "|" + i);
        }
        long duration = (Calendar.getInstance().getTimeInMillis() - begining);
        System.out.println("Duration: " + duration + " milliseconds");
    }

    @Test
    public void ddsAnalysisTest() {

        consumerService.startupListener();

        int partial1;
        int partial2 = 0;
        Number received = 0;
        while (received.intValue() == 0) {
            partial1 = consumerService.getMessageAmmount();
            System.out.println("p1:" + partial1 + " | p2:" + partial2);
            if (partial1 == partial2) {
                received = consumerService.getMessageAmmount();
            } else {
                partial2 = partial1;
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(" ");
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        System.out.println(" ");
//        System.out.println("Duration: " + (duration/1000) + " seconds");
        System.out.println("   Sent   |  Received  | Loss | Speed ");
        System.out.print(totalOfSentMessages + " | ");
        System.out.print(received + " | ");
        System.out.print((100f - ((received.floatValue() * 100f) / totalOfSentMessages.floatValue())) + "% | ");
//        System.out.println((totalOfSentMessages / duration) + " msg/millisecond");

    }

}
