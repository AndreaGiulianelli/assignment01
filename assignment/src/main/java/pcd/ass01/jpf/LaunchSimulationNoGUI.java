package pcd.ass01.jpf;

import gov.nasa.jpf.vm.Verify;
import pcd.ass01.barrierversion.controller.passive.*;
import pcd.ass01.barrierversion.model.EnvironmentModel;

import java.util.ArrayList;
import java.util.List;

public class LaunchSimulationNoGUI {
    public static void main(String... args) {
        Verify.beginAtomic();

        // Initialize simulation values
        final int iterations = 2;
        final int nBodies = 6;
        // Initialize model
        final EnvironmentModel model = new EnvironmentModelImpl(-4, -4, 4, 4);
        //model.initialize(iterations, Stream.iterate(10, i -> 10).limit(nBodies).collect(Collectors.toList()));
        final List<Integer> masses = new ArrayList<>();
        for(int i = 0; i < nBodies; i++) {masses.add(10);}
        model.initialize(iterations, masses);
        // Initialize startstop
        final StartAndStopNotifier startAndStopNotifier = new FakeStartStop();
        // Initialize controller
        final Controller controller = new ControllerImpl(model, startAndStopNotifier);
        // Set view in the controller
        controller.setView(null);

        Verify.endAtomic();

        // Initialize controller active part
        final Master master = new Master(model, null, startAndStopNotifier);
        master.start();
        try {
            master.join();
        } catch (InterruptedException e) {}

        // Check JPF properties
        /*
            Check that every body has been performed two calculations corresponding to the two
            unit of work: force and pos
         */
        assert ((EnvironmentModelImpl) model).getBodiesComputed() == nBodies * iterations * 2;

    }
}
