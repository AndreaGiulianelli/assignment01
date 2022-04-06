package pcd.ass01.barrierversion.controller.active;

import pcd.ass01.barrierversion.controller.passive.CyclicBarrier;
import pcd.ass01.barrierversion.controller.passive.CyclicLatch;
import pcd.ass01.barrierversion.controller.passive.StartAndStopListener;
import pcd.ass01.barrierversion.model.Body;
import pcd.ass01.barrierversion.model.Boundary;
import pcd.ass01.barrierversion.model.EnvironmentModel;
import pcd.ass01.barrierversion.model.V2d;

import java.util.LinkedList;
import java.util.List;

public class Worker extends Thread{
    private static final boolean DEBUG = false;
    private final EnvironmentModel envModel;
    private final StartAndStopListener startAndStopListener;
    private final CyclicBarrier posBarrier;
    private final CyclicBarrier forceBarrier;
    private final CyclicLatch completedLatch;
    private final int startIndex;
    private final int nBodyAllocated;

    protected Worker(final EnvironmentModel envModel, final StartAndStopListener startAndStopListener,
                     final CyclicBarrier posBarrier, final CyclicBarrier forceBarrier, final CyclicLatch completedLatch,
                     final int startIndex, final int nBodyAllocated) {
        this.envModel = envModel;
        this.startAndStopListener = startAndStopListener;
        this.posBarrier = posBarrier;
        this.forceBarrier = forceBarrier;
        this.completedLatch = completedLatch;
        this.startIndex = startIndex;
        this.nBodyAllocated = nBodyAllocated;
    }

    @Override
    public void run() {
        this.log("started");
        // Caching general value used during simulation that remain the same
        final double dt = this.envModel.getDeltaTime();
        final Boundary boundary = this.envModel.getBounds();
        final List<Body> bodies = this.envModel.getBodies();
        final LinkedList<Body> bodyToCompute = new LinkedList<>();
        // Caching the body ref to compute by the worker (they will remain the same for the entire simulation)
        for(int i = 0; i < this.nBodyAllocated; i++) {
            final Body body = this.envModel.getBody(startIndex + i);
            bodyToCompute.addFirst(body);
        }
        // with the try catch the thread can be interrupted and can exit from computation when needed
        try {
            while (true) {
                // Check if we have to stop
                startAndStopListener.startGateWait();
                // Wait in order to proceed calculating the forces on new positions
                this.log("wait position to became consistent");
                this.posBarrier.hitAndWait();
                // Calculate forces
                this.log("calculate forces");
                for(final Body body : bodyToCompute) {
                    body.updateAcceleration(computeTotalForceOnBody(body, bodies));
                }
                // Wait every worker finish the force calculus
                this.log("wait for other workers to compute forces");
                this.forceBarrier.hitAndWait();
                // Update positions
                this.log("update positions");
                for(final Body body : bodyToCompute) {
                    body.updateVelocity(dt);
                    body.updatePos(dt);
                    body.checkAndSolveBoundaryCollision(boundary);
                }
                // Notify latch (so master) for the completion of one iteration on the bodies provided
                this.log("iteration completed");
                this.completedLatch.countDown();
            }
        } catch (InterruptedException e) {}
    }

    private V2d computeTotalForceOnBody(final Body b, final List<Body> bodies) {
        V2d totalForce = new V2d(0, 0);

        /* compute total repulsive force */
        for (int j = 0; j < bodies.size(); j++) {
            Body otherBody = bodies.get(j);
            if (!b.equals(otherBody)) {
                try {
                    V2d forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (Exception ex) {
                }
            }
        }

        /* add friction force */
        totalForce.sum(b.getCurrentFrictionForce());

        return totalForce;
    }

    private void log(final String stringToLog) {
        if(Worker.DEBUG) {
            synchronized (System.out) {
                System.out.println("[worker: " + this.getName() + "]: " + stringToLog);
            }
        }
    }
}
