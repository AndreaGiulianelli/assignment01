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
            }
        }
    }
}
