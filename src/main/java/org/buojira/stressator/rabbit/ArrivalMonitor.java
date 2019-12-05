package org.buojira.stressator.rabbit;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import com.fluig.broker.exception.BrokerException;

public class ArrivalMonitor implements Runnable {
    private final MessageProducerService producerService;


    public ArrivalMonitor(MessageProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    public void run() {
        wait4It(10 * 1000);
        Map<String, Number> map = MessageRepository.getInstance().getQueueAgeMap();
        while (!map.isEmpty()) {
            System.out.println("Checking for undelivered messages  ****************** ");
            Object[] keys = map.keySet().toArray();

            for (int i = 0; i < keys.length; i++) {
                String key = String.valueOf(keys[i]);
                Number sentTime = map.get(key);
                if (isItWaitingForTooLong(sentTime)) {

                    System.out.println(" **************** ");
                    System.out.println("'" + key + "' was lost in time and space. Sending it again");
                    System.out.println(" **************** ");

                    try {
                        producerService.sendSomething(key);
                    } catch (BrokerException e) {
                        e.printStackTrace();
                    }
                }
            }
            wait4It(5 * 1000);
        }
    }

    private boolean isItWaitingForTooLong(Number sentTime) {
        if (sentTime != null) {
            long current = Calendar.getInstance().getTimeInMillis();
            return current - sentTime.longValue() > 30000;
        } else {
            return false;
        }
    }

    protected void wait4It(Number milliseconds) {
        try {
            Thread.sleep(milliseconds.longValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
