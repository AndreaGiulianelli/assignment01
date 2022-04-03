package pcd.ass01.barrierversion.controller.passive;

/**
 * Monitor to handle start and stop of the simulation
 */
public class StartStop implements StartAndStopNotifier{
    private boolean run;

    public StartStop() {
        this.run = false;
    }

    @Override
    public synchronized void startGateWait() throws InterruptedException {
        while(!this.run) {
            this.wait();
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return this.run;
    }

    @Override
    public synchronized void notifyStart() {
        this.run = true;
        this.notifyAll();
    }

    @Override
    public synchronized void notifyStop() {
        this.run = false;
    }
}
