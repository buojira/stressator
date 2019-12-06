package org.buojira.stressator;

import java.text.ParseException;

import org.buojira.stressator.rabbit.service.BrokerOverloadService;
import org.buojira.stressator.rabbit.BrokerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StressatorService {

    private BrokerProperties brokerProperties;

    @Autowired
    private BrokerOverloadService overloadService;

    public StressatorService(BrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
    }

    public void stress(ActionDecider actionDecider) throws ParseException {
        mergeProperties(actionDecider);
        if (actionDecider.isTestRabbitMQDDS()) {
            stressRabbitMQ(actionDecider.getDuration());
        } else if (actionDecider.isClearRabbitMQ()) {
            Number[] durations = actionDecider.getDurations();
            Number[] totals = actionDecider.getTotals();
            analyseRabbitServer(durations, totals);
        } else if (actionDecider.isSendNWait()) {
            stressButNotThatMuch(actionDecider.getDuration());
        }
    }

    private void mergeProperties(ActionDecider actionDecider) {
        if (actionDecider.getRabbitMQHost() != null) {
            brokerProperties.setBrokerHost(actionDecider.getRabbitMQHost());
        }
        if (actionDecider.getRabbitMQPort() != null) {
            brokerProperties.setBrokerPort(String.valueOf(actionDecider.getRabbitMQPort()));
        }
        if (actionDecider.getRabbitMQUSER() != null) {
            brokerProperties.setBrokerUserName(actionDecider.getRabbitMQUSER());
        }
        if (actionDecider.getRabbitMQPassword() != null) {
            brokerProperties.setBrokerPassword(actionDecider.getRabbitMQPassword());
        }
        if (actionDecider.getRabbitAppExchange() != null) {
            brokerProperties.setExchangeName(actionDecider.getRabbitAppExchange());
        }
        if (actionDecider.getRabbitAppQueue() != null) {
            brokerProperties.setQueueName(actionDecider.getRabbitAppQueue());
        }
        if (actionDecider.getRabbitStatusQueue() != null) {
            brokerProperties.setBrokerStatusQueue(actionDecider.getRabbitStatusQueue());
        }
    }

    private void stressRabbitMQ(Number duration) {

        System.out.println(" ");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println("so... stress rabbit we will");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println(" ");

        try {
            overloadService.ddsThresholdTest(duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stressButNotThatMuch(Number duration) {

        System.out.println(" ");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println("let us do it calmly");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println(" ");

        try {
            overloadService.sendAndWait(brokerProperties, duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyseRabbitServer(Number[] durations, Number[] totals) {

        System.out.println(" ");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println(" cleaning up rabbit server ");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println(" ");

        try {
            overloadService.ddsAnalysisTest(brokerProperties, durations, totals);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
