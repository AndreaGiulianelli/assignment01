package pcd.ass01.barrierversion.controller.passive;

/**
 * A barrier that reset itself when passed.
 */
public class CyclicBarrier {
    private int nRemainedParticipants;
    private final int originalParticipants;
    private int awakeAfterWait;

    public CyclicBarrier(final int nParticipants) {
        this.nRemainedParticipants = nParticipants;
        this.originalParticipants = nParticipants;
        this.awakeAfterWait = 0;
    }

    public synchronized void hitAndWait() throws InterruptedException {
        // due to E = W < S signaling. It could occur that for example this.awakeAfterWait > 0
        // and one thread (that has already pass the barrier but at the same time there are others that are unlocked and the scheduler
        // hasn't assigned the cpu to them yet) enter the monitor (so if it enters will decrease this.nRemainedParticipants under 0)
        while (this.awakeAfterWait > 0) {
            this.wait();
        }
        this.nRemainedParticipants--;
        if (this.nRemainedParticipants == 0) {
            this.notifyAll();
        } else {
            while(this.nRemainedParticipants != 0) {
                this.wait();
            }
        }
        this.awakeAfterWait++;
        this.checkAwake();
    }

    private void checkAwake() {
        // Check that is the last to awake after the wait
        if (this.awakeAfterWait == this.originalParticipants) {
            // The last to awake will reset the barrier
            this.nRemainedParticipants = this.originalParticipants;
            this.awakeAfterWait = 0;
            this.notifyAll(); // Notify process that are trying to enter monitor during the awaking phase.
        }
    }
}
