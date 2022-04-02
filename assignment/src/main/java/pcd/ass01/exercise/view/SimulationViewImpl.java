package pcd.ass01.exercise.view;

import pcd.ass01.exercise.controller.passive.Controller;
import pcd.ass01.seq.Boundary;
import pcd.ass01.seq.P2d;

import java.util.List;

/**
 * View designed as a monitor
 */
public class SimulationViewImpl implements SimulationView{
    private final SimulationViewer viewer;

    public SimulationViewImpl(final Controller controller) {
        this.viewer = new SimulationGUI(controller);
    }

    @Override
    public void display(List<P2d> positions, double vt, long iter, Boundary boundary) {

    }
}
