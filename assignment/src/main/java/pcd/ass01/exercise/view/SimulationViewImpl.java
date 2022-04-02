package pcd.ass01.exercise.view;

import pcd.ass01.exercise.controller.passive.Controller;

/**
 * View designed as a monitor
 */
public class SimulationViewImpl implements SimulationView{
    private final SimulationViewer viewer;

    public SimulationViewImpl(final Controller controller) {
        this.viewer = new SimulationGUI(controller);
    }

}
