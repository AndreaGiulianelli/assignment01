package pcd.ass01.barrierversion;

import pcd.ass01.barrierversion.controller.active.Master;
import pcd.ass01.barrierversion.controller.passive.Controller;
import pcd.ass01.barrierversion.controller.passive.ControllerImpl;
import pcd.ass01.barrierversion.controller.passive.StartAndStopNotifier;
import pcd.ass01.barrierversion.controller.passive.StartStop;
import pcd.ass01.barrierversion.model.EnvironmentModel;
import pcd.ass01.barrierversion.model.EnvironmentModelImpl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LaunchSimulationNoGUI {
    public static void main(String... args) {
        // Initialize simulation values
        final int iterations = 1000;
        final int nBodies = 100;
        // Initialize model
        final EnvironmentModel model = new EnvironmentModelImpl(-6, -6, 6, 6);
        model.initialize(iterations, Stream.iterate(10, i -> 10).limit(nBodies).collect(Collectors.toList()));
        // Initialize startstop
        // final StartAndStopNotifier startAndStopNotifier = new FakeStartStop();
        final StartAndStopNotifier startAndStopNotifier = new StartStop();
        // Initialize controller
        final Controller controller = new ControllerImpl(model, startAndStopNotifier);
        // Set view in the controller
        controller.setView(null);
        // Initialize controller active part
        new Master(model, null, startAndStopNotifier).start();
        startAndStopNotifier.notifyStart();
    }
}
