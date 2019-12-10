package org.buojira.stressator;

import java.text.ParseException;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 This class serves just for the project compile something.
 You may remove it as soon as you want.
 **/
@SpringBootApplication
@EnableCaching
public class StressatorApplication {

    @Autowired
    private StressatorService stressatorService;

    private BrokerProperties brokerProperties;

    private static String[] PARAMS;

    public static void main(String[] args) {
        System.out.println(" ");
        System.out.println("Go, ninja, go.");
        System.out.println(" ");
        PARAMS = args;
        SpringApplication.run(StressatorApplication.class, args);
    }

    public StressatorApplication(BrokerProperties brokerProperties,
            StressatorService stressatorService) {
        this.stressatorService = stressatorService;
        this.brokerProperties = brokerProperties;
        try {
            stressatorService.stress(brokerProperties, new ActionDecider(PARAMS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
