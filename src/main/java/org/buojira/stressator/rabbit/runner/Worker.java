package org.buojira.stressator.rabbit.runner;

public abstract class Worker implements Runnable {

    private boolean finished;

    public Worker() {
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

}
