package pcd.ass01.exercise.controller.passive;

public class FakeStartStop implements StartStopNotifier{
    @Override
    public void notifyStart() {

    }

    @Override
    public void notifyStop() {

    }

    @Override
    public void startGateWait() {

    }

    @Override
    public boolean isRunning() {
        return true;
    }
}
