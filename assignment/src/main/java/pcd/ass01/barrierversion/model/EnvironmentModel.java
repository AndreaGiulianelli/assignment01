package pcd.ass01.barrierversion.model;

import java.util.List;

// todo: check that all methods are needed
public interface EnvironmentModel {
    /**
     * Initialize the model creating all the bodies
     * @param masses the mass of each body to create
     */
    void initialize(int maxIterations, List<Integer> masses);
    /**
     * Increment environmentIterations
     */
    void incrementIterations();
    /**
     * Get how many bodies are in the simulation
     * @return the bodyCount
     */
    int getBodiesCount();
    /**
     * Get a list of the bodies
     * @return the list
     */
    List<Body> getBodies();
    /**
     * Get a single body from the environment
     * @param id
     * @return
     */
    Body getBody(int id);
    /**
     * Get boundary of the environment
     * @return the bounds
     */
    Boundary getBounds();
    /**
     * Get dt in our virtual time
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
