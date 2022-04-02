package pcd.ass01.exercise.controller.passive;

public interface StartStopNotifier extends StartStopWaiter {
    void notifyStart();
    void notifyStop();
}
