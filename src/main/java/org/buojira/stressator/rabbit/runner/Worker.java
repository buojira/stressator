package org.buojira.stressator.rabbit.runner;

import org.buojira.stressator.rabbit.BrokerProperties;
import org.buojira.stressator.rabbit.PropCustomizer;

public abstract class Worker implements Runnable {

    private boolean finished;
    private final PropCustomizer propCustomizer;

    public Worker() {
        propCustomizer = new PropCustomizer();
        this.finished = false;
    }

    @Override
    public void run() {
        try {
            work();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.finished = true;
        }

    }

    protected abstract void work() throws Exception;

    public boolean isFinished() {
        return finished;
    }

    protected BrokerProperties customizeProps(BrokerProperties source, String prefix) {
        return propCustomizer.customizeProps(source, prefix);
    }

}
