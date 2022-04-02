package pcd.ass01.exercise.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Model not needed to be a monitor
 */
public class EnvironmentModelImpl implements EnvironmentModel{
    private static final double DELTA_TIME = 0.001;
    private final Boundary boundary;
    private List<Body> bodies;
    private int iteration;
    private double vt = 0; // Virtual time

    public EnvironmentModelImpl(double x0, double y0, double x1, double y1) {
        this.boundary = new Boundary(x0, y0, x1, y1);
        this.iteration = 0;
    }

    @Override
    public void initialize(final List<Integer> masses) {
        // todo uncooment
        final int bodiesCount = masses.size();
        this.bodies = new ArrayList<>(bodiesCount);
        final Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < bodiesCount; i++) {
            double x = this.boundary.getX0()*0.25 + rand.nextDouble() * (this.boundary.getX1() - this.boundary.getX0()) * 0.25;
            double y = this.boundary.getY0()*0.25 + rand.nextDouble() * (this.boundary.getY1() - this.boundary.getY0()) * 0.25;
            Body b = new Body(i, new P2d(x, y), new V2d(0, 0), masses.get(i));
            this.bodies.add(b);
        }

        //todo delete
//        bodies = new ArrayList<Body>();
//        bodies.add(new Body(0, new P2d(-0.1, 0), new V2d(0,0), 1));
//        bodies.add(new Body(1, new P2d(0.1, 0), new V2d(0,0), 2));
    }

    @Override
    public void incrementIterations() {
        this.iteration++;
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
        return this.iteration;
    }
}
