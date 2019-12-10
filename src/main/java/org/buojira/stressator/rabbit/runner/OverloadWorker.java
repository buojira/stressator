package org.buojira.stressator.rabbit.runner;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.service.BrokerOverloadService;

public class OverloadWorker extends Worker {

    private final BrokerOverloadService overloader;
    private final Number duration;
    private final BrokerProperties properties;
    private final String type;
    private final int index;

    public OverloadWorker(BrokerOverloadService overloader,
            Number duration,
            BrokerProperties properties,
            String type,
            int index) {

        this.overloader = overloader;
        this.duration = duration;
        this.properties = properties;
        this.type = type;
        this.index = index;

    }

    @Override
    protected void work() throws Exception {

        BrokerProperties props = customizeProps(properties, type + index);

        Worker sender = overloader.sendAndWait(props, duration);

        Number waitingTime = (duration.floatValue() * 1000) + 5000;
        Thread.sleep(waitingTime.longValue());

        Worker cleaner = overloader.clearQueues(props);

        while (!sender.isFinished() && !cleaner.isFinished()) {
            Thread.sleep(5000l);
        }

    }

}
