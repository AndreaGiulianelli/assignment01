package pcd.ass01.exercise.controller.generic.task;

import pcd.ass01.exercise.controller.passive.StartStopWaiter;

/**
 * Interface that describe a generic task executed by a worker
 */
public interface Task {
    /**
     * Method to execute the task (it's executed in the calling thread).
     * @param startStopWaiter is passed by the executor because in this way we can control the start
     *                        and the stop based on the state of the worker.
     */
    void execute(StartStopWaiter startStopWaiter);
}
