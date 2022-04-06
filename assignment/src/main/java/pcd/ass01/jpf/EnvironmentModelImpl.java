package pcd.ass01.jpf;

import gov.nasa.jpf.annotation.FilterField;
import pcd.ass01.barrierversion.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Model not needed to be a monitor
 */
public class EnvironmentModelImpl implements EnvironmentModel {
    private static final double DELTA_TIME = 0.001;
    private final Boundary boundary;
    private List<Body> bodies;
    private int currentIteration;
    private int maxIterations;
    private double vt = 0; // Virtual time

    // JPF
    private int bodiesComputed = 0;

    public EnvironmentModelImpl(final double x0, final double y0, final double x1, final double y1) {
        this.boundary = new Boundary(x0, y0, x1, y1);
        this.currentIteration = 0;
    }

    @Override
    public void initialize(final int maxIterations, final List<Integer> masses) {
        this.maxIterations = maxIterations;
        final int bodiesCount = masses.size();
        this.bodies = new ArrayList<>(bodiesCount);
        final Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < bodiesCount; i++) {
            final double x = this.boundary.getX0()*0.25 + rand.nextDouble() * (this.boundary.getX1() - this.boundary.getX0()) * 0.25;
            final double y = this.boundary.getY0()*0.25 + rand.nextDouble() * (this.boundary.getY1() - this.boundary.getY0()) * 0.25;
            final Body b = new Body(i, new P2d(x, y), new V2d(0, 0), masses.get(i));
            this.bodies.add(b);
        }
    }

    @Override
    public void incrementIterations() {
        this.currentIteration++;
        this.vt += EnvironmentModelImpl.DELTA_TIME;
    }

    @Override
    public int getBodiesCount() {
        return this.bodies.size();
    }

    @Override
    public List<Body> getBodies() {
        return this.bodies;
    }

    @Override
    public Body getBody(int id) {
        return this.bodies.get(id);
    }

    @Override
    public Boundary getBounds() {
        return this.boundary;
    }

    @Override
    public double getDeltaTime() {
        return EnvironmentModelImpl.DELTA_TIME;
    }

    @Override
    public double getVirtualTime() {
        return this.vt;
    }

    @Override
    public int getIterationCount() {
        return this.currentIteration;
    }

    @Override
    public boolean isSimulationOver() {
        return this.currentIteration >= this.maxIterations;
    }


    // JPF - these two method are synchronized because they are accessed by different threads.
    // These two methods are an addition to the model (that doesn't need to be a monitor) only for JPF tests purposes.
    public synchronized void bodyComputed() {
        this.bodiesComputed++;
    }

    public synchronized int getBodiesComputed() {
        return this.bodiesComputed;
    }
}
