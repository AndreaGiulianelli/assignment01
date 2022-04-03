package pcd.ass01.barrierversion.controller.passive;

/**
 * A latch that reset itself when unlocked
 * It's a latch in witch a single thread wait.
 */
public class CyclicLatch {
    private int count;
    private final int original;

    public CyclicLatch(final int count) {
        this.count = count;
        this.original = count;
    }

    public synchronized void countDown() {
        this.count--;
        if (this.count == 0) {
            this.notifyAll();
        }
    }

    public synchronized void await() throws InterruptedException {
        while(this.count != 0) {
            this.wait();
        }
        // Reset the latch
        this.count = this.original;
    }
}
