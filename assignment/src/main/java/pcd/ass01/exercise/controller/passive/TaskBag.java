package pcd.ass01.exercise.controller.passive;

import pcd.ass01.exercise.controller.generic.task.Task;

import java.util.LinkedList;



public class TaskBag {
    private LinkedList<Task> forceTaskList;
    private LinkedList<Task> computedForceTaskList;
    private LinkedList<Task> posTaskList;
    private LinkedList<Task> computedPosTaskList;
    private LinkedList<Task> currentList;
    private TaskTurn turn;

    public TaskBag() {
        this.forceTaskList = new LinkedList<>();
        this.computedForceTaskList = new LinkedList<>();
        this.posTaskList = new LinkedList<>();
        this.computedPosTaskList = new LinkedList<>();
        this.turn = TaskTurn.NULL;
        this.currentList = this.forceTaskList;
    }

    public synchronized void reset() {
        this.forceTaskList = this.computedForceTaskList;
        this.computedForceTaskList = new LinkedList<>();
        this.posTaskList = this.computedPosTaskList;
        this.computedPosTaskList = new LinkedList<>();
        this.currentList = this.forceTaskList;
        this.turn = TaskTurn.NULL;
    }

    public synchronized void setTaskTurn(final TaskTurn status) {
        this.turn = status;
        if(this.turn.equals(TaskTurn.FORCE)) {
            this.currentList = this.forceTaskList;
        } else {
            this.currentList = this.posTaskList;
        }
        this.notifyAll();
    }

    public synchronized void addForceTask(final Task task) {
        this.forceTaskList.addLast(task);
        //this.notifyAll();
    }

    public synchronized void addPosTask(final Task task) {
        this.posTaskList.addLast(task);
        //this.notifyAll();
    }

    public synchronized Task getTask() {
        while(this.currentList.isEmpty() || this.turn.equals(TaskTurn.NULL)) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
        Task task = this.currentList.removeFirst();
        if(this.turn.equals(TaskTurn.FORCE)) {
            this.computedForceTaskList.addLast(task);
        } else {
            this.computedPosTaskList.addLast(task);
        }
        return task;
    }

    public enum TaskTurn {
        NULL,
        FORCE,
        POS;
    }
}
