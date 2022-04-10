package pcd.ass01.barrierversion.controller.passive;

import pcd.ass01.barrierversion.model.EnvironmentModel;
import pcd.ass01.barrierversion.view.SimulationView;

public class ControllerImpl implements Controller{
    private final EnvironmentModel model;
    private final StartAndStopNotifier startAndStopNotifier;
    private SimulationView view;

    public ControllerImpl(final EnvironmentModel model, final StartAndStopNotifier startAndStopNotifier) {
        this.model =model;
        this.startAndStopNotifier = startAndStopNotifier;
    }

    @Override
    public synchronized void setView(SimulationView view) {
        this.view = view;
    }

    @Override
    public synchronized void notifyStart() {
        this.startAndStopNotifier.notifyStart();
    }

    @Override
    public synchronized void notifyStop() {
        this.startAndStopNotifier.notifyStop();
    }
}
