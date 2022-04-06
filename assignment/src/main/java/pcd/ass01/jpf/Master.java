package pcd.ass01.jpf;

import gov.nasa.jpf.vm.Verify;
import pcd.ass01.barrierversion.controller.passive.CyclicBarrier;
import pcd.ass01.barrierversion.controller.passive.CyclicLatch;
import pcd.ass01.barrierversion.controller.passive.StartAndStopListener;
import pcd.ass01.barrierversion.model.Boundary;
import pcd.ass01.barrierversion.model.EnvironmentModel;
import pcd.ass01.barrierversion.view.SimulationView;

import java.util.LinkedList;
import java.util.List;

public class Master extends Thread{
    private final EnvironmentModel model;
    private final SimulationView view;
    private final StartAndStopListener startAndStopListener;

    public Master(final EnvironmentModel model, final SimulationView view, final StartAndStopListener startAndStopListener) {
        Verify.beginAtomic();
        this.model = model;
        this.view = view;
        this.startAndStopListener = startAndStopListener;
        Verify.endAtomic();
    }

    @Override
    public void run() {
        Verify.beginAtomic();

        final int nWorkers = 2;
        // Create barrier and latch
        final CyclicBarrier posBarrier = new CyclicBarrier(nWorkers + 1);
        final CyclicBarrier forceBarrier = new CyclicBarrier(nWorkers);
        final CyclicLatch iterationCompletedLatch = new CyclicLatch(nWorkers);
        // Initialize and start workers
        final int bodiesPerWorker = this.model.getBodiesCount() / nWorkers;
        int currentStartIndex = 0;
        final List<Worker> workerList = new LinkedList<>();

        Verify.endAtomic();

        for(int i = 0; i < nWorkers - 1; i++) {
            Verify.beginAtomic();

            final Worker worker = new WorkerBuilder()
                    .setEnvModel(this.model)
                    .setStartAndStopListener(this.startAndStopListener)
                    .setPosBarrier(posBarrier)
                    .setForceBarrier(forceBarrier)
                    .setCompletedLatch(iterationCompletedLatch)
                    .setStartIndex(currentStartIndex)
                    .setNBodyAllocated(bodiesPerWorker)
                    .build();
            workerList.add(worker);
            currentStartIndex += bodiesPerWorker;

            Verify.endAtomic();
            worker.start();
        }
        Verify.beginAtomic();

        // Remained bodies in the last worker
        final Worker lastWorker = new WorkerBuilder()
                .setEnvModel(this.model)
                .setStartAndStopListener(this.startAndStopListener)
                .setPosBarrier(posBarrier)
                .setForceBarrier(forceBarrier)
                .setCompletedLatch(iterationCompletedLatch)
                .setStartIndex(currentStartIndex)
                .setNBodyAllocated(this.model.getBodiesCount() - currentStartIndex)
                .build();
        workerList.add(lastWorker);

        Verify.endAtomic();
        lastWorker.start();

        // Caching values
        final Boundary boundary = this.model.getBounds();

        try {
            while (!this.model.isSimulationOver()) {
                // check if the simulation need to be stopped
                this.startAndStopListener.startGateWait();
                // inform ready to update position
                posBarrier.hitAndWait();
                // wait iteration completed on the latch
                iterationCompletedLatch.await();

                // JPF
                // Here we can execute safe copy
                // Check that no worker is working
                assert Utility.getInstance().getWorkerInPos() + Utility.getInstance().getWorkerInForce() == 0;

                // increase the iteration count
                this.model.incrementIterations();
            }
        } catch (InterruptedException e) {}

        Verify.beginAtomic();
        // Inform the view
        if (view != null) {
            this.view.simulationEnd();
        }
        Verify.endAtomic();

        Verify.beginAtomic();
        // Interrupt all the workers in order to complete
        for(final Worker w: workerList) {
            w.interrupt();
        }
        Verify.endAtomic();
    }
}
