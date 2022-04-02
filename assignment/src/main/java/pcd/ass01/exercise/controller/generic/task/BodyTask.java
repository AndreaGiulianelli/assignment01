package pcd.ass01.exercise.controller.generic.task;

import pcd.ass01.exercise.controller.passive.BodyForceUpdater;
import pcd.ass01.exercise.controller.passive.CyclicLatch;
import pcd.ass01.exercise.controller.passive.StartStopWaiter;
import pcd.ass01.exercise.controller.passive.TaskBag;
import pcd.ass01.exercise.model.Body;
import pcd.ass01.exercise.model.EnvironmentModel;

/**
 * BodyTask represent the task for handle a single body.
 * It will create all the tasks necessary to calculate the forces.
 */
public class BodyTask implements Task{
    private final TaskBag taskBag;
    private final BodyForceUpdater bodyForceUpdater;
    private final EnvironmentModel envModel;
    private final CyclicLatch latch;
    private final Body body;

    public BodyTask(final TaskBag taskBag, final EnvironmentModel envModel, final Body body, final BodyForceUpdater bodyForceUpdater, final CyclicLatch latch) {
        this.taskBag = taskBag;
        this.envModel = envModel;
        this.body = body;
        this.bodyForceUpdater = bodyForceUpdater;
        this.latch = latch;
    }

    @Override
    public void execute(StartStopWaiter startStopWaiter) {
        // Check if the worker that execute the task can effectively continue.
        startStopWaiter.startGateWait();
        // Create task to calculate friction force
        final Task frictionForceTask = new FrictionTask(this.body, this.bodyForceUpdater, this.latch);
        this.taskBag.addTask(frictionForceTask);

        // Create task to calculate repulsive force
        for(Body by : this.envModel.getBodies()) {
            final Task repulsiveForceTask = new RepulsiveTask(this.body, by, this.bodyForceUpdater, this.latch);
            this.taskBag.addTask(repulsiveForceTask);
        }
    }
}
