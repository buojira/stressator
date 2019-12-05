package org.buojira.stressator.rabbit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Queue;

import org.buojira.stressator.Formatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fluig.broker.exception.BrokerException;

@Service
public class BrokerOverloadService {

    @Autowired
    private MessageProducerService producerService;

    @Autowired
    private MessageConsumerService consumerService;

    public Map<String, Number> startUpListeners(BrokerProperties properties) {
        if (properties != null) {
            consumerService.startupSecureProcessingListener(producerService, properties);
            consumerService.startupStatusListener(properties);
        } else {
            consumerService.startupSecureProcessingListener(producerService);
            consumerService.startupStatusListener();
        }
        new Thread(new ArrivalMonitor(producerService)).start();
        return MessageRepository.getInstance().getQueueAgeMap();
    }

    public String send(BrokerProperties props, String hostName, long messageCount) throws BrokerException {
        String key = hostName + "|" + messageCount;
        if (props != null) {
            producerService.sendSomething(props, key);
        } else {
            producerService.sendSomething(key);
        }
        if (messageCount % 5000 == 0) {
            System.out.println(Formatter.DECIMAL_FORMAT.format(messageCount) + " messages sent.");
        }
        return key;
    }

    public void sendAndControlPayload(Number duration) throws UnknownHostException, BrokerException, InterruptedException {
        sendAndControlPayload(null, duration);
    }

    public void sendAndWait(BrokerProperties props, Number duration) throws UnknownHostException, BrokerException, InterruptedException {

        final Number timeLimit = (duration != null) ? (duration.floatValue() * 1000) : 5000;
        long beginning = Calendar.getInstance().getTimeInMillis();
        long current = beginning;
        long messageCount = 0;
        String hostName = InetAddress.getLocalHost().getHostName();

        while ((current - beginning) < timeLimit.longValue()) {
            messageCount++;
            current = Calendar.getInstance().getTimeInMillis();
            String key = hostName + "|" + messageCount;
            producerService.queueMessage(props, key);
        }

        System.out.println(" ");
        System.out.println(" ***************************** ");
        System.out.println(messageCount + " queued Messages");
        System.out.println(" ***************************** ");
        System.out.println(" ");

        producerService.processQueue(props);

        Queue<String> cache = MessageRepository.getInstance().getMessageCache();
        while (!cache.isEmpty()) {
            System.out.println(" ***************************** ");
            System.out.println("> Cache Size: " + cache.size());
            System.out.println(" ***************************** ");
            Thread.sleep(2000L);
        }

    }

    public void sendAndControlPayload(BrokerProperties props, Number duration) throws UnknownHostException, BrokerException, InterruptedException {

        Map<String, Number> map = startUpListeners(props);

        final Number timeLimit = (duration != null) ? (duration.floatValue() * 1000) : 5000;
        long beginning = Calendar.getInstance().getTimeInMillis();
        long current = beginning;
        long messageCount = 0;
        String hostName = InetAddress.getLocalHost().getHostName();

        while ((current - beginning) < timeLimit.longValue()) {
            messageCount++;
            current = Calendar.getInstance().getTimeInMillis();
            map.put(send(props, hostName, messageCount), current);
//            Thread.sleep(500L);
        }

        writeDDSReport(current - beginning, hostName, messageCount);
        System.out.println("> Map Size: " + map.size());

        System.out.println(" ");
        System.out.println(" ***************************** ");
        System.out.println(" ***************************** ");
        System.out.println(" ");

        System.out.printf("> Now let us check what happened");
        while (!map.isEmpty()) {
            System.out.println("> Map Size: " + map.size());
            Thread.sleep(1000L);
        }
        System.out.println("> And we are done here");
/*
        System.out.println(" ");
        System.out.println(" ***************************** ");
        System.out.println(" ***************************** ");
        System.out.println(" ");
        System.out.println(" Total Messages Sent: " + messageCount);
        System.out.println(" Messages Sent Again: " + messageCount);
        System.out.println(" ");
        System.out.println(" ***************************** ");
        System.out.println(" ***************************** ");
        System.out.println(" ");

 */


    }

    public void ddsThresholdTest(Number duration) throws BrokerException, UnknownHostException {

        NumberFormat formatter = Formatter.DECIMAL_FORMAT;

        final long timeLimit = (duration != null) ? (duration.longValue() * 1000) : 5000;

        long beginning = Calendar.getInstance().getTimeInMillis();
        long current = beginning;

        String hostName = InetAddress.getLocalHost().getHostName();
        long messageCount = 0;

        while ((current - beginning) < timeLimit) {
            messageCount++;
            current = Calendar.getInstance().getTimeInMillis();
            producerService.sendSomething(hostName + "|" + messageCount);
            if (messageCount % 5000 == 0) {

                System.out.println(formatter.format(messageCount) + " messages sent.");

            }
        }
        writeDDSReport(current - beginning, hostName, messageCount);
    }

    public void ddsAnalysisTest(Number[] durations, Number[] totals) {

        NumberFormat formatter = Formatter.DECIMAL_FORMAT;

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

        writeAnalysisReport(duration, totalOfSentMessages, received);

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

    private void writeDDSReport(long timeTaken, String hostName, long messageCount) {
        NumberFormat formatter = Formatter.DECIMAL_FORMAT;

        System.out.println(" ");
        System.out.println("---------------------------------");
        System.out.println("------------- REPORT ------------");
        System.out.println("---------------------------------");
        System.out.println(" ");
        System.out.println("   Sent By: " + hostName);
        System.out.println("  Duration: " + formatter.format(
                timeTaken
        ) + " milliseconds");
        System.out.println("Total Sent: " + formatter.format(
                messageCount
        ) + " messages");
        System.out.println("      Rate: " + formatter.format(
                (messageCount / timeTaken)
        ) + " messages per millisecond");
        System.out.println(" ");
        System.out.println("---------------------------------");
        System.out.println("---------- REPORT's END ---------");
        System.out.println("---------------------------------");
        System.out.println(" ");
    }

    private void writeAnalysisReport(Number duration, Number totalOfSentMessages, Number received) {
        NumberFormat formatter = Formatter.DECIMAL_FORMAT;

        System.out.println(" ");
        System.out.println("------------------------------------------");
        System.out.println("------------- ANALYSIS REPORT ------------");
        System.out.println("------------------------------------------");
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
        System.out.println(" ");
        System.out.println(" ++++ ");
        System.out.println(" ");
        Map<String, Long> map = consumerService.getCounterMap();
        for (String key : map.keySet()) {
            Long value = map.get(key);
            System.out.println(key + " | " + value);
        }
        System.out.println(" ");
        System.out.println(" ++++ ");
        System.out.println(" ");
        System.out.println("------------------------------------------");
        System.out.println("---------- ANALYSIS REPORT's END ---------");
        System.out.println("------------------------------------------");
        System.out.println(" ");
    }

}
