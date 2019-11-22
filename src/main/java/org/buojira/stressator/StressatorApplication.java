package org.buojira.stressator;

import java.text.ParseException;

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

    private static String[] PARAMS;

    public static void main(String[] args) {
        System.out.println(" ");
        System.out.println("Go, ninja, go.");
        System.out.println(" ");
        PARAMS = args;
        SpringApplication.run(StressatorApplication.class, args);
    }

    public StressatorApplication(StressatorService stressatorService) {
        this.stressatorService = stressatorService;
        try {
            stressatorService.stress(new ActionDecider(PARAMS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
