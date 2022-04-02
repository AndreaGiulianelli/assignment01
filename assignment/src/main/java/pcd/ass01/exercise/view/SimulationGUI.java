package pcd.ass01.exercise.view;

import pcd.ass01.exercise.controller.passive.Controller;

public class SimulationGUI implements SimulationViewer{

    private final Controller controller;

    public SimulationGUI(final Controller controller) {
        this.controller = controller;
    }
}
