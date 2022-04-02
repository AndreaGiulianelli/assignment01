package pcd.ass01.exercise.controller.passive;

/**
 * Monitor to handle start and stop of the simulation
 * Applying the ISP with StartStopNotifier and StartStopWaiter interfaces
 */
public class StartStop implements StartStopNotifier, StartStopWaiter{
    private boolean run;

    public StartStop() {
        this.run = false;
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

    @Override
    public synchronized boolean isRunning() {
        return this.run;
    }

    @Override
    public synchronized void startGateWait() {
        while(!this.run) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
    }
}
