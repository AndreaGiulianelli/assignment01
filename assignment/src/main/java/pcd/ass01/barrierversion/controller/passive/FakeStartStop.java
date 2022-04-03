package pcd.ass01.barrierversion.controller.passive;

/**
    Fake start and stop
 */
public class FakeStartStop implements StartAndStopNotifier{
    @Override
    public void startGateWait() throws InterruptedException {}

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void notifyStart() {}

    @Override
    public void notifyStop() {}
}
