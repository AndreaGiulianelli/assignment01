package pcd.ass01.exercise.controller.active;

import pcd.ass01.exercise.controller.generic.task.BodyTask;
import pcd.ass01.exercise.controller.generic.task.PositionTask;
import pcd.ass01.exercise.controller.generic.task.Task;
import pcd.ass01.exercise.controller.passive.BodyForceUpdater;
import pcd.ass01.exercise.controller.passive.CyclicLatch;
import pcd.ass01.exercise.controller.passive.StartStopWaiter;
import pcd.ass01.exercise.controller.passive.TaskBag;
import pcd.ass01.exercise.model.Body;
import pcd.ass01.exercise.model.EnvironmentModel;
import pcd.ass01.exercise.model.P2d;
import pcd.ass01.exercise.view.SimulationView;

import java.util.List;
import java.util.stream.Collectors;

public class Master extends Thread{
    private static final boolean DEBUG = true;
    private final EnvironmentModel envModel;
    private final StartStopWaiter startStopWaiter;
    private final SimulationView simulationView;
    private final int nIteration;

    public Master(final int nIteration, final EnvironmentModel envModel, final StartStopWaiter startStopWaiter, final SimulationView simulationView) {
        this.nIteration = nIteration;
        this.envModel = envModel;
        this.startStopWaiter = startStopWaiter;
        this.simulationView = simulationView;
    }

    @Override
    public void run() {
        log("started");
        // Establish the number of the workers
        final int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        // Create the task bag
        final TaskBag taskBag = new TaskBag();
        // Create the latch
        final CyclicLatch latch = new CyclicLatch(this.envModel.getBodiesCount());
        // Create workers
        for(int i = 0; i < nWorkers; i++) {
            final Worker worker = new Worker(taskBag, this.startStopWaiter);
            worker.start();
        }

        // Work
        while(this.envModel.getIterationCount() < nIteration) {
            this.startStopWaiter.startGateWait();
            // Set up
            latch.reset();
            taskBag.clear();
            // Assign force jobs to workers (check startstop)
            log("creating force tasks");
            for (final Body body : this.envModel.getBodies()) {
                //this.startStopWaiter.startGateWait();
                final BodyForceUpdater bodyForceUpdater = new BodyForceUpdater(this.envModel.getBodiesCount() - 1);
                final Task taskToAdd = new BodyTask(taskBag, this.envModel, body, bodyForceUpdater, latch);
                taskBag.addTask(taskToAdd);
            }
            // Wait force jobs to finish on latch
            log("waiting force tasks to complete");
            latch.await();
            // clear latch and bag (it's already cleared)
            latch.reset();
            taskBag.clear();
            // Assign pos jobs to workers (check startstops)
            log("creating position tasks");
            for (final Body body : this.envModel.getBodies()) {
                //this.startStopWaiter.startGateWait();
                final Task taskToAdd = new PositionTask(body, latch, this.envModel);
                taskBag.addTask(taskToAdd);
            }
            // Wait pos jobs to finish on latch
            log("waiting position tasks to complete");
            latch.await();

            this.envModel.incrementIterations();
            //check startstop and send update to view (send a safe copy of the bodies (maybe a map into positions))
            if(this.startStopWaiter.isRunning()) {
                log("send iteration to GUI");
                final List<P2d> posSafeCopy = this.envModel.getBodies().stream().map(b -> new P2d(b.getPos())).collect(Collectors.toList());
                // todo: send view
                // this.simulationView
            } else {
                // Send info to GUI that is stopped
                log("stopped");
            }
        }
        log("done");
    }

    private void log(final String stringToLog) {
        if(Master.DEBUG) {
            synchronized (System.out) {
                System.out.println("[master: " + this.getName() + "]: " + stringToLog);
            }
        }
    }
}
