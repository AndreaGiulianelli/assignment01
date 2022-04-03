package pcd.ass01.exercise.controller.generic.task;

import pcd.ass01.exercise.controller.passive.BodyForceUpdater;
import pcd.ass01.exercise.controller.passive.CyclicLatch;
import pcd.ass01.exercise.controller.passive.StartStopWaiter;
import pcd.ass01.exercise.model.Body;
import pcd.ass01.exercise.model.InfiniteForceException;
import pcd.ass01.exercise.model.V2d;

public class RepulsiveTask implements Task{
    private final Body to;
    private final Body by;
    private final BodyForceUpdater bodyForceUpdater;
    private final CyclicLatch latch;

    public RepulsiveTask(final Body to, final Body by, final BodyForceUpdater bodyForceUpdater, final CyclicLatch latch) {
        this.to = to;
        this.by = by;
        this.bodyForceUpdater = bodyForceUpdater;
        this.latch = latch;
    }

    @Override
    public void execute(StartStopWaiter startStopWaiter) {
        // Check if the worker that execute the task can effectively continue.
        startStopWaiter.startGateWait();
        try {
            // Update repulsive forces and if the calculation of all forces for the body is completed call countDown on the latch
            final V2d force = this.to.computeRepulsiveForceBy(by);
            if (this.bodyForceUpdater.updateRepulsive(force)) {
                // We have all the force, we can safely update the acceleration
                this.to.updateAcceleration(this.bodyForceUpdater.getTotalForceAndReset());
                // Inform the latch that we have completed a body until acceleration
                this.latch.countDown();
            }
        } catch (InfiniteForceException e) {}
    }
}
