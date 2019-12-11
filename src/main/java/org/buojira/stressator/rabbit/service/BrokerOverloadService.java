package org.buojira.stressator.rabbit.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Map;

import org.buojira.stressator.Formatter;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.MessageRepository;
import org.buojira.stressator.rabbit.runner.ConsumersMonitor;
import org.buojira.stressator.rabbit.runner.ManyConnectionsWorker;
import org.buojira.stressator.rabbit.runner.OneAtATimeSender;
import org.buojira.stressator.rabbit.runner.QueueCleaner;
import org.buojira.stressator.rabbit.runner.Worker;
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

        new Thread(
                new ConsumersMonitor(
                        producerService,
                        consumerService,
                        properties)
        ).start();
        return MessageRepository
                .getInstance()
                .getQueueAgeMap();

    }

    public void alwaysUseNewConnectionAndKeepItOpened(BrokerProperties brokerProperties, int index) {
        new Thread(
                new ManyConnectionsWorker(
                        brokerProperties,
                        index,
                        producerService,
                        consumerService
                )
        ).start();
    }

    public String send(BrokerProperties props, String hostName, long messageCount) throws BrokerException {

        String key = hostName + "|" + messageCount;
        if (props != null) {
            producerService.sendSomething(props, key);
        } else {
            producerService.sendSomething(props, key);
        }
        if (messageCount % 5000 == 0) {
            System.out.println(Formatter.DECIMAL_FORMAT.format(messageCount) + " messages sent.");
        }
        return key;

    }

    public Worker sendAndWait(BrokerProperties props, Number duration) {

        Worker worker = new OneAtATimeSender(
                producerService,
                props,
                duration);

        new Thread(worker).start();

        return worker;

    }

    public QueueCleaner clearQueues(BrokerProperties props) {

        QueueCleaner cleaner = new QueueCleaner(consumerService, props);

        new Thread(cleaner).start();

        return cleaner;

    }

    public void ddsAnalysisTest(BrokerProperties props, Number[] durations, Number[] totals) throws InterruptedException {

        final Number duration = sumUp(durations);
        final Number totalOfSentMessages = sumUp(totals);

        QueueCleaner cleaner = clearQueues(props);
        while (!cleaner.isFinished()) {
            System.out.println(">> wait for it...");
            Thread.sleep(2000l);
        }

        writeAnalysisReport(duration, totalOfSentMessages, cleaner.getReceived());

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

    }

    public void ddsThresholdTest(BrokerProperties props, Number duration) throws Exception {

        NumberFormat formatter = Formatter.DECIMAL_FORMAT;

        final long timeLimit = (duration != null) ? (duration.longValue() * 1000) : 5000;

        long beginning = Calendar.getInstance().getTimeInMillis();
        long current = beginning;

        String hostName = InetAddress.getLocalHost().getHostName();
        long messageCount = 0;

        while ((current - beginning) < timeLimit) {
            messageCount++;
            current = Calendar.getInstance().getTimeInMillis();
            producerService.sendSomething(props, hostName + "|" + messageCount);
            if (messageCount % 5000 == 0) {

                System.out.println(formatter.format(messageCount) + " messages sent.");

            }
            Thread.sleep(1l);
        }
        writeDDSReport(current - beginning, hostName, messageCount);
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
