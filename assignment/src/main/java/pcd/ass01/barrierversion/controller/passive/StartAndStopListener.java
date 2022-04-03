package pcd.ass01.barrierversion.controller.passive;

public interface StartAndStopListener {
    /**
     * Wait on the start gate in order to continue.
     * If it's in stop mode, the thread that call this method will wait.
     */
    void startGateWait() throws InterruptedException;
    /**
     * Non-blocking way to check if it's stopped or not
     * @return true if it's running, false if it has been stopped.
     */
    boolean isRunning();
}
