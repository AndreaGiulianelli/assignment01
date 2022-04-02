package pcd.ass01.exercise.controller.passive;

import pcd.ass01.exercise.controller.generic.task.Task;

import java.util.LinkedList;

public class TaskBag {
    private final LinkedList<Task> taskList;

    public TaskBag() {
        this.taskList = new LinkedList<>();
    }

    public synchronized void clear() {
        this.taskList.clear();
    }

    public synchronized void addTask(final Task task) {
        this.taskList.addLast(task);
        this.notifyAll();
    }

    public synchronized Task getTask() {
        while(this.taskList.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
        return this.taskList.removeFirst();
    }
}
