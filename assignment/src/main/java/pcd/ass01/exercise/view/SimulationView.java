package pcd.ass01.exercise.view;

import pcd.ass01.seq.Boundary;
import pcd.ass01.seq.P2d;

import java.util.List;

public interface SimulationView {
    /**
     *
     * @param positions list of the position of the bodies
     * @param vt virtual time
     * @param iter
     * @param boundary
     */
    void display(List<P2d> positions, double vt, long iter, Boundary boundary);
}
