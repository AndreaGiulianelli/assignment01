package pcd.ass01.exercise.controller.passive;

import pcd.ass01.exercise.model.EnvironmentModel;
import pcd.ass01.exercise.view.SimulationView;

/**
 * Passive controller, designed as a monitor.
 */
public class ControllerImpl implements Controller{
    private final EnvironmentModel model;
    private final StartStop startStop;
    private SimulationView view;

    public ControllerImpl(final EnvironmentModel model, final StartStop startStop) {
        this.model = model;
        this.startStop = startStop;
    }

    @Override
    public synchronized void setView(SimulationView view) {
        this.view = view;
    }
}
