package pcd.ass01.exercise.view;

import pcd.ass01.exercise.controller.passive.Controller;
import pcd.ass01.exercise.model.Boundary;
import pcd.ass01.exercise.model.P2d;
import pcd.ass01.exercise.view.gui.SimulationGUI;

import java.util.List;

/**
 * View designed as a monitor
 */
public class SimulationViewImpl implements SimulationView{
    private final SimulationViewer viewer;

    public SimulationViewImpl(int w, int h, final Controller controller) {
        this.viewer = new SimulationGUI(w, h, controller);
    }

    @Override
    public synchronized void display() {
        this.viewer.display();
    }

    @Override
    public synchronized void update(final List<P2d> positions, final double vt, final long iter, final Boundary boundary) {
        this.viewer.update(positions, vt, iter, boundary);
    }
}
