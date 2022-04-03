package pcd.ass01.exercise;

import pcd.ass01.exercise.controller.active.Master;
import pcd.ass01.exercise.controller.passive.*;
import pcd.ass01.exercise.model.EnvironmentModel;
import pcd.ass01.exercise.model.EnvironmentModelImpl;
import pcd.ass01.exercise.view.SimulationView;
import pcd.ass01.exercise.view.SimulationViewImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main class to start the simulator
 */
public class LaunchSimulator {
    public static void main(String... args) {
        // Initialize model
        final EnvironmentModel model = new EnvironmentModelImpl(-6.0, -6.0, 6.0, 6.0);
        model.initialize(Stream.iterate(10, i -> 10).limit(500).collect(Collectors.toList()));
        // Initialize startstop
        //final StartStopNotifier startStop = new StartStop();
        final StartStopNotifier startStop = new FakeStartStop();
        // Initialize controller (passive part)
        final Controller controller = new ControllerImpl(model, startStop);
        // Initialize view
        final SimulationView view = new SimulationViewImpl(620, 620, controller);
        // Set view in the controller
        controller.setView(view);
        // Initialize controller (active part)
        new Master(50000, model, startStop, view).start();
        startStop.notifyStart();

        view.display();
    }

}
