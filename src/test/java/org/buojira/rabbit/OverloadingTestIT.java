package org.buojira.rabbit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import org.buojira.stressator.rabbit.service.MessageConsumerService;
import org.buojira.stressator.rabbit.service.MessageProducerService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fluig.broker.exception.BrokerException;

public class OverloadingTestIT extends StressatorBaseIT {

    @Autowired
    private MessageProducerService producerService;

    @Autowired
    private MessageConsumerService consumerService;

    private final NumberFormat formatter = new DecimalFormat("#,###.######");

    private static final Number TIME_LIMIT = 1000 * 5;

    private final Number[] durations = {5000};
    private final Number[] totals = {9115, 9428};

    @Test
    public void ddsThresholdTest() throws BrokerException, UnknownHostException {
        long beginning = Calendar.getInstance().getTimeInMillis();
        long current = beginning;
        String hostName = InetAddress.getLocalHost().getHostName();
        long messageCount = 0;
        while ((current - beginning) < TIME_LIMIT.longValue()) {
            messageCount++;
            current = Calendar.getInstance().getTimeInMillis();
            producerService.sendSomething(hostName + "|" + messageCount);
            if (messageCount % 5000 == 0) {
                System.out.println(formatter.format(messageCount) + " messages sent.");
            }
        }
        float duration = current - beginning;

        System.out.println(" ");
        System.out.println("---------------------------------");
        System.out.println("------------- REPORT ------------");
        System.out.println("---------------------------------");
        System.out.println(" ");
        System.out.println("   Sent By: " + hostName);
        System.out.println("  Duration: " + formatter.format(
                duration
        ) + " milliseconds");
        System.out.println("Total Sent: " + formatter.format(
                messageCount
        ) + " messages");
        System.out.println("      Rate: " + formatter.format(
                (messageCount / duration)
        ) + " messages per millisecond");
    }

    @Test
    public void ddsAnalysisTest() {

        final Number duration = sumUp(durations);
        final Number totalOfSentMessages = sumUp(totals);

        consumerService.startupProcessingListener();

        int partial1;
        int partial2 = 0;
        Number received = 0;
        while (received.intValue() == 0) {
            partial1 = consumerService.getTotalAmmount();

            System.out.println("p1:"
                    + formatter.format(partial1)
                    + " | p2:"
                    + formatter.format(partial2));

            if (partial2 > 0 && partial1 == partial2) {
                received = consumerService.getTotalAmmount();
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
        System.out.println("------------- REPORT ------------");
        System.out.println("---------------------------------");
        System.out.println(" ");
        System.out.println("     Duration: " + formatter.format(
                (duration.floatValue() / 1000)) + " seconds"
        );
        System.out.println("    Qtd. Sent: " + formatter.format(
                totalOfSentMessages
        ));
        System.out.println("Qtd. Received: " + formatter.format(
                received
        ));
        System.out.println("   Perc. Loss: " + formatter.format(
                (100f - ((received.floatValue() * 100f) / totalOfSentMessages.floatValue()))
        ) + "%");
        System.out.println("         Rate: " + formatter.format(
                (totalOfSentMessages.floatValue() / duration.floatValue())
        ) + " msg/millisecond");

    }

    private Number sumUp(Number[] values) {
        Number result = 0;
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                result = result.floatValue() + values[i].floatValue();
            }
        }
        return result;
    }

}
