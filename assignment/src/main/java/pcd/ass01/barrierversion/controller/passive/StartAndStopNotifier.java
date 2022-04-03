package pcd.ass01.barrierversion.controller.passive;

public interface StartAndStopNotifier extends StartAndStopListener{
    /**
     * Notify start
     */
    void notifyStart();
    /**
     * Notify stop
     */
    void notifyStop();
}
