package pcd.ass01.barrierversion.controller.active;

import pcd.ass01.barrierversion.controller.passive.CyclicBarrier;
import pcd.ass01.barrierversion.controller.passive.CyclicLatch;
import pcd.ass01.barrierversion.controller.passive.StartAndStopListener;
import pcd.ass01.barrierversion.model.Boundary;
import pcd.ass01.barrierversion.model.EnvironmentModel;
import pcd.ass01.barrierversion.model.P2d;
import pcd.ass01.barrierversion.view.SimulationView;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Master extends Thread{
    private static final boolean DEBUG = false;
    private final EnvironmentModel model;
    private final SimulationView view;
    private final StartAndStopListener startAndStopListener;

    public Master(final EnvironmentModel model, final SimulationView view, final StartAndStopListener startAndStopListener) {
        this.model = model;
        this.view = view;
        this.startAndStopListener = startAndStopListener;
    }

    @Override
    public void run() {
        final int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        // Create barrier and latch
        final CyclicBarrier posBarrier = new CyclicBarrier(nWorkers + 1);
        final CyclicBarrier forceBarrier = new CyclicBarrier(nWorkers);
        final CyclicLatch iterationCompletedLatch = new CyclicLatch(nWorkers);
        // Initialize and start workers
        final int bodiesPerWorker = this.model.getBodiesCount() / nWorkers;
        int currentStartIndex = 0;
        final List<Worker> workerList = new LinkedList<>();
        for(int i = 0; i < nWorkers - 1; i++) {
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
            worker.start();
        }
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
        lastWorker.start();

        // Caching values
        final Boundary boundary = this.model.getBounds();
        long t0 = System.currentTimeMillis();
        try {
            while (!this.model.isSimulationOver()) {
                // check if the simulation need to be stopped
                this.startAndStopListener.startGateWait();
                // inform ready to update position
                log("going to init a new iteration");
                posBarrier.hitAndWait();
                // wait iteration completed on the latch
                log("wait iteration to complete");
                iterationCompletedLatch.await();

                // increase the iteration count
                this.model.incrementIterations();

                log("iteration completed, save consistent results");
                if(this.view != null) {
                    // save position in order to be consistent in the visualization
                    final List<P2d> posSafeCopy = this.model.getBodies().stream().map(b -> new P2d(b.getPos())).collect(Collectors.toList());
                    // display
                    log("send iteration to GUI");
                    this.view.update(posSafeCopy, this.model.getVirtualTime(), this.model.getIterationCount(), boundary);
                }
            }
        } catch (InterruptedException e) {}
        long t1 = System.currentTimeMillis();
        System.out.println("Time: " + (t1 - t0) + "ms");
        // Inform the view
        if (this.view != null) {
            this.view.simulationEnd();
        }
        // Interrupt all the workers in order to complete
        for(final Worker w: workerList) {
            w.interrupt();
        }
    }

    private void log(final String stringToLog) {
        if(Master.DEBUG) {
            synchronized (System.out) {
                System.out.println("[master: " + this.getName() + "]: " + stringToLog);
            }
        }
    }
}
