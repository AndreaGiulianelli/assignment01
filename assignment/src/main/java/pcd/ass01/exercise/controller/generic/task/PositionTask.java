package pcd.ass01.exercise.controller.generic.task;

import pcd.ass01.exercise.controller.passive.CyclicLatch;
import pcd.ass01.exercise.controller.passive.StartStopWaiter;
import pcd.ass01.exercise.model.Body;
import pcd.ass01.exercise.model.EnvironmentModel;

public class PositionTask implements Task{
    private final Body body;
    private final CyclicLatch latch;
    private final EnvironmentModel envModel;

    public PositionTask(final Body body, final CyclicLatch latch, final EnvironmentModel envModel) {
        this.body = body;
        this.latch = latch;
        this.envModel = envModel;
    }

    @Override
    public void execute(StartStopWaiter startStopWaiter) {
        // Check if the worker that execute the task can effectively continue.
        startStopWaiter.startGateWait();
        double dt = this.envModel.getDeltaTime();
        this.body.updateVelocity(dt);
        this.body.updatePos(dt);
        this.body.checkAndSolveBoundaryCollision(this.envModel.getBounds());
        // countDown to the latch in order to signal the termination of the calculation for the position of the body
        this.latch.countDown();
    }
}
