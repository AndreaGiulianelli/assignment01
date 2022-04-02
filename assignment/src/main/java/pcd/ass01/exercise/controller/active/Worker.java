package pcd.ass01.exercise.controller.active;

import pcd.ass01.exercise.controller.generic.task.Task;
import pcd.ass01.exercise.controller.passive.StartStopWaiter;
import pcd.ass01.exercise.controller.passive.TaskBag;

/**
 * Generic worker.
 * Task strategy is passed.
 */
public class Worker extends Thread{
    private static final boolean DEBUG = false;
    private final TaskBag taskBag;
    private final StartStopWaiter startStopWaiter;

    public Worker(final TaskBag taskBag, final StartStopWaiter startStopWaiter) {
        this.taskBag = taskBag;
        this.startStopWaiter = startStopWaiter;
    }

    @Override
    public void run() {
        this.log("started");
        while(true) {
            final Task task = this.taskBag.getTask();
            log("task allocated");

            // Check if we can continue
            this.startStopWaiter.startGateWait();
            log("can do the task");
            // Execute the task
            task.execute(this.startStopWaiter);
            log("task done");
        }
    }

    private void log(final String stringToLog) {
        if(Worker.DEBUG) {
            synchronized (System.out) {
                System.out.println("[worker: " + this.getName() + "]: " + stringToLog);
            }
        }
    }

}
