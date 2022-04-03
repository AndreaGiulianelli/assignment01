package pcd.ass01.barrierversion.view;

import pcd.ass01.barrierversion.model.Boundary;
import pcd.ass01.barrierversion.model.P2d;

import java.util.List;

public interface SimulationView {
    /**
     * Display the view
     */
    void display();
    /**
     * Update the view
     * @param positions list of the position of the bodies
     * @param vt virtual time
     * @param iter iteration counter
     * @param boundary boundary of the environment
     */
    void update(List<P2d> positions, double vt, long iter, Boundary boundary);
    /**
     * Method to inform the view that the simulation is ended
     */
    void simulationEnd();
}
