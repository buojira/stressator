package org.buojira.stressator.rabbit.runner;

import java.util.Calendar;
import java.util.Queue;

import org.buojira.stressator.file.RabbitMessage;
import org.buojira.stressator.file.RabbitMessageBuilder;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.MessageRepository;
import org.buojira.stressator.rabbit.service.MessageProducerService;

public class OneAtATimeSender extends Worker {

    private final MessageProducerService producerService;
    private final BrokerProperties props;
    private final Number timeLimit;

    public OneAtATimeSender(MessageProducerService producerService,
            BrokerProperties props,
            Number duration) {
        this.producerService = producerService;
        this.props = props;
        timeLimit = (duration != null) ? (duration.floatValue() * 1000) : 5000;

    }

    @Override
    protected void work() throws Exception {

        long beginning = Calendar.getInstance().getTimeInMillis();
        long current = beginning;

        Queue<RabbitMessage> queue = MessageRepository
                .getInstance()
                .getRabbitMessagesCache();

        while ((current - beginning) < timeLimit.longValue()) {
            current = Calendar.getInstance().getTimeInMillis();
            queue.offer(RabbitMessageBuilder.build());
        }

        System.out.println(" ");
        System.out.println(" ***************************** ");
        System.out.println(queue.size() + " queued Messages");
        System.out.println(" ***************************** ");
        System.out.println(" ");

        producerService.processRabbitQueue(props);

        while (!queue.isEmpty()) {
            System.out.println(" ***************************** ");
            System.out.println("> Cache Size: " + queue.size());
            System.out.println(" ***************************** ");
            Thread.sleep(2000L);
        }

    }

}
