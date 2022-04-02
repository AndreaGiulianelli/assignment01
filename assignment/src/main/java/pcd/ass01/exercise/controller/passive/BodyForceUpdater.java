package pcd.ass01.exercise.controller.passive;

import pcd.ass01.exercise.model.V2d;

/**
 * Passive component that handle the concurrent update of the forces by the workers.
 * It represents the partial result on which the workers are working during the fork-join.
 */
public class BodyForceUpdater {
    private final V2d totalForce;
    private boolean frictionCalculated;
    private int repulsiveForceCounter;

    public BodyForceUpdater(final int repulsiveForceCounter) {
        this.totalForce = new V2d(0, 0);
        this.frictionCalculated = false;
        this.repulsiveForceCounter = repulsiveForceCounter;
    }

    public synchronized boolean updateRepulsive(final V2d repulsive) {
        this.totalForce.sum(repulsive);
        this.repulsiveForceCounter--;
        return this.isUpdateCompleted();
    }

    public synchronized boolean updateFriction(final V2d friction) {
        this.totalForce.sum(friction);
        this.frictionCalculated = true;
        return this.isUpdateCompleted();
    }

    public synchronized V2d getTotalForce() {
        return this.totalForce;
    }

    private boolean isUpdateCompleted() {
        return this.repulsiveForceCounter == 0 && this.frictionCalculated;
    }
}
