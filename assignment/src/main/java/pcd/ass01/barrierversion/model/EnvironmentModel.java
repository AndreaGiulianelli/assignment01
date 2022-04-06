package pcd.ass01.barrierversion.model;

import java.util.List;

/**
 * Interface that the model has to offer in order to work with the simulation environment.
 */
public interface EnvironmentModel {
    /**
     * Initialize the model creating all the bodies
     * @param maxIterations number of the simulation's interations.
     * @param masses the mass of each body to create
     */
    void initialize(int maxIterations, List<Integer> masses);
    /**
     * Increment environment iteration counter
     */
    void incrementIterations();
    /**
     * Get the number of bodies that are in the simulation
     * @return the bodies number
     */
    int getBodiesCount();
    /**
     * Get a list of the bodies
     * @return the list
     */
    List<Body> getBodies();
    /**
     * Get a single body from the environment
     * @param id the id of the body to retrieve
     * @return the body
     */
    Body getBody(int id);
    /**
     * Get boundary of the environment
     * @return the bounds
     */
    Boundary getBounds();
    /**
     * Get the delta-time of our virtual time
     * @return the delta-time
     */
    double getDeltaTime();
    /**
     * Get the virtual time of the simulation
     * @return the virtual time
     */
    double getVirtualTime();
    /**
     * Get the actual iteration number
     * @return the iteration number
     */
    int getIterationCount();
    /**
     * Check if the simulation is completed
     * @return true if the simulation is completed, false instead
     */
    boolean isSimulationOver();
}
