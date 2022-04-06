package pcd.ass01.barrierversion;

import pcd.ass01.barrierversion.controller.active.Master;
import pcd.ass01.barrierversion.controller.passive.Controller;
import pcd.ass01.barrierversion.controller.passive.ControllerImpl;
import pcd.ass01.barrierversion.controller.passive.FakeStartStop;
import pcd.ass01.barrierversion.controller.passive.StartAndStopNotifier;
import pcd.ass01.barrierversion.model.EnvironmentModel;
import pcd.ass01.barrierversion.model.EnvironmentModelImpl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LaunchSimulationNoGUI {
    public static void main(String... args) {
        // Initialize simulation values
        final int iterations = 1000;
        final int nBodies = 1000;
        final int mass = 10;
        // Initialize model
        final EnvironmentModel model = new EnvironmentModelImpl(-6, -6, 6, 6);
        model.initialize(iterations, Stream.iterate(mass, i -> mass).limit(nBodies).collect(Collectors.toList()));
        // Initialize startstop
        final StartAndStopNotifier startAndStopNotifier = new FakeStartStop();
        // Initialize controller
        final Controller controller = new ControllerImpl(model, startAndStopNotifier);
        // Set view in the controller
        controller.setView(null);
        // Initialize controller active part
        new Master(model, null, startAndStopNotifier).start();
    }
}
