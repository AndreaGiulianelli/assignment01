package pcd.ass01.jpf;

/**
 * Utility class used for some JPF counters
 * Designed as a singleton monitor because is accessed by different threads.
 */
public class Utility {
    private static Utility INSTANCE = null;

    private int workerInPos = 0; // Worker currently in pos calculation
    private int workerInForce = 0; // Worker currently in force calculation

    private Utility() {}

    public synchronized static Utility getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Utility();
        }
        return INSTANCE;
    }

    public synchronized void workerInPos() {
        this.workerInPos++;
    }

    public synchronized void workerOutPos() {
        this.workerInPos--;
    }

    public synchronized int getWorkerInPos() {
        return this.workerInPos;
    }

    public synchronized void workerInForce() {
        this.workerInForce++;
    }

    public synchronized void workerOutForce() {
        this.workerInForce--;
    }

    public synchronized int getWorkerInForce() {
        return this.workerInForce;
    }
}
