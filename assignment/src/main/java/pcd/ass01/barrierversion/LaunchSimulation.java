package pcd.ass01.barrierversion;

import pcd.ass01.barrierversion.controller.active.Master;
import pcd.ass01.barrierversion.controller.passive.*;
import pcd.ass01.barrierversion.model.EnvironmentModel;
import pcd.ass01.barrierversion.model.EnvironmentModelImpl;
import pcd.ass01.barrierversion.view.SimulationView;
import pcd.ass01.barrierversion.view.SimulationViewImpl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LaunchSimulation {
    public static void main(String... args) {
        // Initialize simulation values
        final int iterations = 2;
        final int nBodies = 20;
        // Initialize model
        final EnvironmentModel model = new EnvironmentModelImpl(-6, -6, 6, 6);
        model.initialize(iterations, Stream.iterate(10, i -> 10).limit(nBodies).collect(Collectors.toList()));
        // Initialize startstop
        final StartAndStopNotifier startAndStopNotifier = new FakeStartStop();
        // final StartAndStopNotifier startAndStopNotifier = new StartStop();
        // Initialize controller
        final Controller controller = new ControllerImpl(model, startAndStopNotifier);
        // Initialize view
        final SimulationView view = new SimulationViewImpl(620, 620, controller);
        // Set view in the controller
        controller.setView(view);
        // Initialize controller active part
        new Master(model, view, startAndStopNotifier).start();
        startAndStopNotifier.notifyStart();

        view.display();
    }
}
