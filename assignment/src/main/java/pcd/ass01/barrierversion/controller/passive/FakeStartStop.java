package pcd.ass01.barrierversion.controller.passive;

/**
    Fake start and stop
 */
public class FakeStartStop implements StartAndStopNotifier{
    @Override
    public synchronized void startGateWait() throws InterruptedException {}

    @Override
    public synchronized boolean isRunning() {
        return true;
    }

    @Override
    public synchronized void notifyStart() {}

    @Override
    public synchronized void notifyStop() {}
}
