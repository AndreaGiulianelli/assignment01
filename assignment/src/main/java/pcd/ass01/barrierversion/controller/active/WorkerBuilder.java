package pcd.ass01.barrierversion.controller.active;

import pcd.ass01.barrierversion.controller.passive.CyclicBarrier;
import pcd.ass01.barrierversion.controller.passive.CyclicLatch;
import pcd.ass01.barrierversion.controller.passive.StartAndStopListener;
import pcd.ass01.barrierversion.model.EnvironmentModel;

import java.util.Optional;

public class WorkerBuilder {
    private boolean built;
    private EnvironmentModel envModel;
    private StartAndStopListener startAndStopListener;
    private CyclicBarrier posBarrier;
    private CyclicBarrier forceBarrier;
    private CyclicLatch completedLatch;
    private Optional<Integer> startIndex;
    private Optional<Integer> nBodyAllocated;

    public WorkerBuilder setEnvModel(final EnvironmentModel envModel) {
        this.envModel = envModel;
        return this;
    }

    public WorkerBuilder setStartAndStopListener(final StartAndStopListener startAndStopListener) {
        this.startAndStopListener = startAndStopListener;
        return this;
    }

    public WorkerBuilder setPosBarrier(final CyclicBarrier posBarrier) {
        this.posBarrier = posBarrier;
        return this;
    }

    public WorkerBuilder setForceBarrier(final CyclicBarrier forceBarrier) {
        this.forceBarrier = forceBarrier;
        return this;
    }

    public WorkerBuilder setCompletedLatch(final CyclicLatch completedLatch) {
        this.completedLatch = completedLatch;
        return this;
    }

    public WorkerBuilder setStartIndex(final int startIndex) {
        this.startIndex = Optional.of(startIndex);
        return this;
    }

    public WorkerBuilder setNBodyAllocated(final int nBodyAllocated) {
        this.nBodyAllocated = Optional.of(nBodyAllocated);
        return this;
    }

    public Worker build() {
        if(!this.built && this.envModel != null && this.startAndStopListener != null
        && this.posBarrier != null && this.forceBarrier != null && this.completedLatch != null
        && this.startIndex.isPresent() && this.nBodyAllocated.isPresent()) {
            this.built = true;
            return new Worker(this.envModel, this.startAndStopListener, this.posBarrier, this.forceBarrier,
                    this.completedLatch, this.startIndex.get(), this.nBodyAllocated.get());
        }
        throw new IllegalStateException();
    }
}
