package org.buojira.stressator.rabbit.service;

import java.util.ArrayList;
import java.util.List;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.PropCustomizer;
import org.buojira.stressator.rabbit.runner.OverloadWorker;
import org.buojira.stressator.rabbit.runner.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParallelOverloadService {

    private BrokerProperties brokerProperties;
    private PropCustomizer propCustomizer;

    @Autowired
    private BrokerOverloadService overloader;

    public ParallelOverloadService(BrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
        propCustomizer = new PropCustomizer();
    }

    public void overloadRabbit(Number duration, BrokerProperties properties, String... types)
            throws InterruptedException {

        if (types != null) {
            List<Worker> workers = startWorkers(duration, properties, types);
            waitUntilAllWorkersFinished(workers);
        }

    }

    public void alwaysUseNewConnectionAndKeepItOpened(
            BrokerProperties brokerProperties,
            Number connectionsAmount)
            throws InterruptedException {

        for (int index = 0; index < connectionsAmount.intValue(); index++) {
            BrokerProperties props = propCustomizer.customizeProps(
                    brokerProperties,
                    "nConn" + index);
            props.setBrokerStatusQueue(null);
            overloader.alwaysUseNewConnectionAndKeepItOpened(
                    props,
                    index
            );
        }

        while (true) {
            System.out.println("I am alive... do not worry");
            Thread.sleep(2000l);
        }

    }

    private List<Worker> startWorkers(Number duration, BrokerProperties properties, String[] types) {

        List<Worker> workers = new ArrayList<>();

        for (int i = 0; i < types.length; i++) {

            String type = types[i];

            if (type != null) {
                Worker worker = new OverloadWorker(
                        overloader,
                        duration,
                        properties,
                        type,
                        i);
                workers.add(worker);
                new Thread(worker).start();
            }

        }

        return workers;

    }

    private void waitUntilAllWorkersFinished(List<Worker> workers) throws InterruptedException {

        while (!workers.isEmpty()) {

            int last = workers.size() - 1;

            for (int i = last; i == 0; i--) {

                Worker worker = workers.get(i);
                if (worker.isFinished()) {
                    workers.remove(i);
                }

            }
            System.out.println("Workers stil working: " + workers.size());
            Thread.sleep(2000l);

        }

    }
}
