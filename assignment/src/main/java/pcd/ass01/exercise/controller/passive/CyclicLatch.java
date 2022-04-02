package pcd.ass01.exercise.controller.passive;

/**
 * A latch that can be reset.
 */
public class CyclicLatch {
    private int count;
    private int original;

    public CyclicLatch(final int count) {
        this.original = count;
        this.count = count;
    }

    public synchronized void countDown() {
        this.count--;
        if(this.count == 0) {
            this.notifyAll();
        }
    }

    public synchronized void await() {
        while(this.count != 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
    }

    public synchronized void reset() {
        this.count = this.original;
    }
}
