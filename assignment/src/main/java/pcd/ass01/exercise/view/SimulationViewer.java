package pcd.ass01.exercise.view;

import pcd.ass01.exercise.model.Boundary;
import pcd.ass01.exercise.model.P2d;

import java.util.List;

/**
 * Interface for describing what a viewer for the Simulator has to implement.
 */
public interface SimulationViewer {
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
}
