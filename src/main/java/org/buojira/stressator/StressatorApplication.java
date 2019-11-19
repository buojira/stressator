package org.buojira.stressator;

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

    public static void main(String[] args) {
        System.out.println("Go, ninja, go.");
        SpringApplication.run(StressatorApplication.class, args);
    }

}
