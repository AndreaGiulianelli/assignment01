package pcd.ass01.exercise.controller.generic.task;

import pcd.ass01.exercise.controller.passive.BodyForceUpdater;
import pcd.ass01.exercise.controller.passive.CyclicLatch;
import pcd.ass01.exercise.controller.passive.StartStopWaiter;
import pcd.ass01.exercise.model.Body;

public class FrictionTask implements Task{
    private final Body body;
    private final BodyForceUpdater bodyForceUpdater;
    private final CyclicLatch latch;

    public FrictionTask(final Body body, final BodyForceUpdater bodyForceUpdater, final CyclicLatch latch) {
        this.body = body;
        this.bodyForceUpdater = bodyForceUpdater;
        this.latch = latch;
    }

    @Override
    public void execute(StartStopWaiter startStopWaiter) {
        // Check if the worker that execute the task can effectively continue.
        startStopWaiter.startGateWait();
        // Update friction and if the calculation of all forces for the body is completed call countDown on the latch
        if (this.bodyForceUpdater.updateFriction(this.body.getCurrentFrictionForce())) {
            // We have all the force, we can safely update the acceleration
            this.body.updateAcceleration(this.bodyForceUpdater.getTotalForceAndReset());
            // Inform the latch that we have completed a body until acceleration
            this.latch.countDown();
        }
    }
}
