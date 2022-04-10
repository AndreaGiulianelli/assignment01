package pcd.ass01.barrierversion.view;

import pcd.ass01.barrierversion.controller.passive.Controller;
import pcd.ass01.barrierversion.model.Boundary;
import pcd.ass01.barrierversion.model.P2d;
import pcd.ass01.barrierversion.view.gui.SimulationGUI;

import java.util.List;

/**
 * View designed as a monitor
 */
public class SimulationViewImpl implements SimulationView{
    private final SimulationViewer viewer;

    public SimulationViewImpl(final int w, final int h, final Controller controller) {
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

    @Override
    public synchronized void simulationEnd() {
        this.viewer.simulationEnd();
    }
}
