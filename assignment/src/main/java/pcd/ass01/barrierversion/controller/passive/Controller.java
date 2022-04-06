package pcd.ass01.barrierversion.controller.passive;

import pcd.ass01.barrierversion.view.SimulationView;

public interface Controller {
    /**
     * Set the view
     * @param view the view.
     */
    void setView(SimulationView view);
    /**
     * Notify the controller that the user want to start/resume the simulation.
     */
    void notifyStart();
    /**
     * Notify the controller that the user want to stop the simulation.
     */
    void notifyStop();
}
