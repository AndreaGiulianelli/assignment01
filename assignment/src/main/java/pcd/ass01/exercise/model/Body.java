package pcd.ass01.exercise.model;


/**
 * Body of the simulation
 */
public class Body {
    private static final double REPULSIVE_CONST = 0.01;
    private static final double FRICTION_CONST = 1;

    private P2d pos;
    private V2d vel;
    private double mass;
    private int id;
    private V2d acceleration; // Acceleration computed

    public Body(int id, P2d pos, V2d vel, double mass){
        this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.mass = mass;
    }

    public double getMass() {
        return this.mass;
    }

    public P2d getPos(){
        return this.pos;
    }

    public V2d getVel(){
        return this.vel;
    }

    public int getId() {
        return this.id;
    }

    public void updateAcceleration(final V2d force) {
        this.acceleration = new V2d(force).scalarMul(1.0 / this.mass);
    }

    public V2d getAcceleration() {
        return this.acceleration;
    }

    public boolean equals(Object b) {
        return ((Body)b).id == id;
    }


    /**
     * Update the position, according to current velocity
     *
     * @param dt time elapsed
     */
    public void updatePos(double dt){
        this.pos.sum(new V2d(this.vel).scalarMul(dt));
    }

    /**
     * Update the velocity, given the instant acceleration
     * @param dt time elapsed
     */
    public void updateVelocity(double dt){
        this.vel.sum(new V2d(this.acceleration).scalarMul(dt));
    }

    /**
     * Change the velocity
     *
     * @param vx
     * @param vy
     */
    public void changeVel(double vx, double vy){
        this.vel.change(vx, vy);
    }

    /**
     * Computes the distance from the specified body
     *
     * @param b
     * @return
     */
    public double getDistanceFrom(Body b) {
        double dx = pos.getX() - b.getPos().getX();
        double dy = pos.getY() - b.getPos().getY();
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     *
     * Compute the repulsive force exerted by another body
     *
     * @param b
     * @return
     * @throws InfiniteForceException
     */
    public V2d computeRepulsiveForceBy(Body b) throws InfiniteForceException {
        double dist = getDistanceFrom(b);
        if (dist > 0) {
            try {
                return new V2d(b.getPos(), pos)
                        .normalize()
                        .scalarMul(b.getMass()*REPULSIVE_CONST/(dist*dist));
            } catch (Exception ex) {
                throw new InfiniteForceException();
            }
        } else {
            throw new InfiniteForceException();
        }
    }

    /**
     *
     * Compute current friction force, given the current velocity
     */
    public V2d getCurrentFrictionForce() {
        return new V2d(vel).scalarMul(-FRICTION_CONST);
    }

    /**
     * Check if there collisions with the boundaty and update the
     * position and velocity accordingly
     *
     * @param bounds
     */
    public void checkAndSolveBoundaryCollision(Boundary bounds){
        double x = pos.getX();
        double y = pos.getY();

        if (x > bounds.getX1()){
            pos.change(bounds.getX1(), pos.getY());
            vel.change(-vel.getX(), vel.getY());
        } else if (x < bounds.getX0()){
            pos.change(bounds.getX0(), pos.getY());
            vel.change(-vel.getX(), vel.getY());
        }

        if (y > bounds.getY1()){
            pos.change(pos.getX(), bounds.getY1());
            vel.change(vel.getX(), -vel.getY());
        } else if (y < bounds.getY0()){
            pos.change(pos.getX(), bounds.getY0());
            vel.change(vel.getX(), -vel.getY());
        }
    }
}
