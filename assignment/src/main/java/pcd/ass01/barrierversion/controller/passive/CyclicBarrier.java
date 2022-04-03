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
        // due to E = W < S signaling. It will occour that for example this.awakeAfterWait > 0
        // and the one that notified try to re-enter (so if it enters will decrease this.nRemainedParticipants under 0)
        while (this.awakeAfterWait > 0) {
            this.wait();
        }
        this.nRemainedParticipants--;
        if (this.nRemainedParticipants == 0) {
            this.notifyAll();
            this.awakeAfterWait++;
        } else {
            while(this.nRemainedParticipants != 0) {
                this.wait();
            }
            this.awakeAfterWait++;
            // Check that is the last to awake after the wait
            if (this.awakeAfterWait == this.originalParticipants) {
                // The last to awake will reset the barrier
                this.nRemainedParticipants = this.originalParticipants;
                this.awakeAfterWait = 0;
                this.notifyAll(); // Notify process that are trying to enter monitor during the reset.
            }
        }
    }
}
