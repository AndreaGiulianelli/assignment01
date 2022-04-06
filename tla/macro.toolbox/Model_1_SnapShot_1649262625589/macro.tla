------------------------------- MODULE macro -------------------------------

EXTENDS TLC, Sequences, Integers, FiniteSets
PT == INSTANCE PT
CONSTANTS Workers, Master

(*--algorithm macro

variables
    simulation = [iterations |-> 2, nBodies |-> 4, completed |-> FALSE],
    iterationsDone = 0,
    bodies \in PT!TupleOf({1}, simulation.nBodies), \* a single body is represent by a 1.
    jobs = [w \in Workers |-> <<>>], \* list of jobs (body) for each worker
    nWorkers = Cardinality(Workers),
    nProcess = nWorkers + 1,
    bodiesComputed = 0, \* number of calculations performed on bodies
    
    workerCanStart = FALSE, \* flag that simulate the start of a process/thread
    posBarrier = [counter |-> 0, needed |-> nProcess, pass |-> FALSE], \* barrier
    forceBarrier = [counter |-> 0, needed |-> nWorkers, pass |-> FALSE], \* barrier
    completedLatch = [counter |-> 0, needed |-> nWorkers]; \* latch
define
    \* Liveness properties
    MasterDoAllIterations == <>[](iterationsDone = simulation.iterations)
    AllBodiesComputed == <>[](bodiesComputed = (simulation.nBodies * simulation.iterations * 2))
    \* Safety invariant (modeled as a TLA+ invariant, we can model it also in the Temporal logic form adding []~ before the formula instead of = FALSE)
    \* Check that workers can't compute on dependent aspects
    SafetyInUpdate == ((\E w \in Workers: pc[w] = "CalculateForceAndAcceleration") /\ (\E w \in Workers: pc[w] = "CalculatePositions")) = FALSE
    \* Check that when the master is processing the result no worker can modify the data (so the processing is consistent)
    SafetyInMasterResultProcess == (pc[Master] = "ProcessLocal" /\ ((\E w \in Workers: pc[w] = "CalculateForceAndAcceleration") \/ (\E w \in Workers: pc[w] = "CalculatePositions"))) = FALSE
end define


fair+ process master = Master
    variables    
        currentIteration = 0;
    begin
        ScheduleWork:
            with workersOrdered = PT!OrderSet(Workers) do
                jobs := [w \in Workers |->
                    LET index == PT!Index(workersOrdered, w) - 1
                    IN  PT!SelectSeqByIndex(bodies, LAMBDA i: i % Len(workersOrdered) = index)
                ];
            end with;
            workerCanStart := TRUE;
        SimulationCicle:
            while simulation.completed = FALSE do
                ForceBarrierReset:
                    await forceBarrier.counter = forceBarrier.needed;
                    rpb: posBarrier.counter := 0; \* reset pos barrier counter
                    rpp: posBarrier.pass := FALSE; \* reset pos barrier pass
                    forceBarrier.pass := TRUE;
                AwaitWorkersComputation:
                    await completedLatch.counter = completedLatch.needed;
                    completedLatch.counter := 0; \* reset latch counter
                ProcessLocal:
                    currentIteration := currentIteration + 1;
                    if currentIteration = simulation.iterations then
                        simulation.completed := TRUE;
                    end if;
                \* Put the pos barrier in the end in order to avoid workers deadlock on the last iteration
                posBarrier.counter := posBarrier.counter + 1;
                OkPos:
                    await posBarrier.counter = posBarrier.needed;
                    rfb: forceBarrier.counter := 0; \* reset force barrier counter
                    rfp: forceBarrier.pass := FALSE; \* reset force barrier pass
                    posBarrier.pass := TRUE;
            end while;
        ProcessSimulationEnd:
            iterationsDone := currentIteration;
end process;

fair+ process worker \in Workers
    variables
        bodyIndex = 1,
        bp = 0; \* number of body processed
    begin 
    WaitToStart: await workerCanStart = TRUE;
    MainLoop:
        while simulation.completed = FALSE do
            CalculateForceAndAcceleration:
                while bodyIndex <= Len(jobs[self]) do
                    BodyProcessForce:
                        bp := bp + jobs[self][bodyIndex];
                        bodyIndex := bodyIndex + 1;
                end while;
                bodyIndex := 1;
            forceBarrier.counter := forceBarrier.counter + 1;
            AwaitForces:
                await forceBarrier.pass = TRUE;
            CalculatePositions:
                while bodyIndex <= Len(jobs[self]) do
                    BodyProcessPos:
                        bp := bp + jobs[self][bodyIndex];
                        bodyIndex := bodyIndex + 1;
                end while;
                bodyIndex := 1;
            NotifyMaster:
                bodiesComputed := bodiesComputed + bp;
                bp := 0;
                completedLatch.counter := completedLatch.counter + 1;
            \* Put the pos barrier in the end in order to avoid workers deadlock on the last iteration
            posBarrier.counter := posBarrier.counter + 1;
            AwaitPosConsistency:
                await posBarrier.pass = TRUE;
        end while;
end process;



end algorithm
*)

\* BEGIN TRANSLATION (chksum(pcal) = "ec788983" /\ chksum(tla) = "52142ae")
VARIABLES simulation, iterationsDone, bodies, jobs, nWorkers, nProcess, 
          bodiesComputed, workerCanStart, posBarrier, forceBarrier, 
          completedLatch, pc

(* define statement *)
MasterDoAllIterations == <>[](iterationsDone = simulation.iterations)
AllBodiesComputed == <>[](bodiesComputed = (simulation.nBodies * simulation.iterations * 2))


SafetyInUpdate == ((\E w \in Workers: pc[w] = "CalculateForceAndAcceleration") /\ (\E w \in Workers: pc[w] = "CalculatePositions")) = FALSE

SafetyInMasterResultProcess == (pc[Master] = "ProcessLocal" /\ ((\E w \in Workers: pc[w] = "CalculateForceAndAcceleration") \/ (\E w \in Workers: pc[w] = "CalculatePositions"))) = FALSE

VARIABLES currentIteration, bodyIndex, bp

vars == << simulation, iterationsDone, bodies, jobs, nWorkers, nProcess, 
           bodiesComputed, workerCanStart, posBarrier, forceBarrier, 
           completedLatch, pc, currentIteration, bodyIndex, bp >>

ProcSet == {Master} \cup (Workers)

Init == (* Global variables *)
        /\ simulation = [iterations |-> 2, nBodies |-> 4, completed |-> FALSE]
        /\ iterationsDone = 0
        /\ bodies \in PT!TupleOf({1}, simulation.nBodies)
        /\ jobs = [w \in Workers |-> <<>>]
        /\ nWorkers = Cardinality(Workers)
        /\ nProcess = nWorkers + 1
        /\ bodiesComputed = 0
        /\ workerCanStart = FALSE
        /\ posBarrier = [counter |-> 0, needed |-> nProcess, pass |-> FALSE]
        /\ forceBarrier = [counter |-> 0, needed |-> nWorkers, pass |-> FALSE]
        /\ completedLatch = [counter |-> 0, needed |-> nWorkers]
        (* Process master *)
        /\ currentIteration = 0
        (* Process worker *)
        /\ bodyIndex = [self \in Workers |-> 1]
        /\ bp = [self \in Workers |-> 0]
        /\ pc = [self \in ProcSet |-> CASE self = Master -> "ScheduleWork"
                                        [] self \in Workers -> "WaitToStart"]

ScheduleWork == /\ pc[Master] = "ScheduleWork"
                /\ LET workersOrdered == PT!OrderSet(Workers) IN
                     jobs' =         [w \in Workers |->
                                 LET index == PT!Index(workersOrdered, w) - 1
                                 IN  PT!SelectSeqByIndex(bodies, LAMBDA i: i % Len(workersOrdered) = index)
                             ]
                /\ workerCanStart' = TRUE
                /\ pc' = [pc EXCEPT ![Master] = "SimulationCicle"]
                /\ UNCHANGED << simulation, iterationsDone, bodies, nWorkers, 
                                nProcess, bodiesComputed, posBarrier, 
                                forceBarrier, completedLatch, currentIteration, 
                                bodyIndex, bp >>

SimulationCicle == /\ pc[Master] = "SimulationCicle"
                   /\ IF simulation.completed = FALSE
                         THEN /\ pc' = [pc EXCEPT ![Master] = "ForceBarrierReset"]
                         ELSE /\ pc' = [pc EXCEPT ![Master] = "ProcessSimulationEnd"]
                   /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                   nWorkers, nProcess, bodiesComputed, 
                                   workerCanStart, posBarrier, forceBarrier, 
                                   completedLatch, currentIteration, bodyIndex, 
                                   bp >>

ForceBarrierReset == /\ pc[Master] = "ForceBarrierReset"
                     /\ forceBarrier.counter = forceBarrier.needed
                     /\ pc' = [pc EXCEPT ![Master] = "rpb"]
                     /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                     nWorkers, nProcess, bodiesComputed, 
                                     workerCanStart, posBarrier, forceBarrier, 
                                     completedLatch, currentIteration, 
                                     bodyIndex, bp >>

rpb == /\ pc[Master] = "rpb"
       /\ posBarrier' = [posBarrier EXCEPT !.counter = 0]
       /\ pc' = [pc EXCEPT ![Master] = "rpp"]
       /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nWorkers, 
                       nProcess, bodiesComputed, workerCanStart, forceBarrier, 
                       completedLatch, currentIteration, bodyIndex, bp >>

rpp == /\ pc[Master] = "rpp"
       /\ posBarrier' = [posBarrier EXCEPT !.pass = FALSE]
       /\ forceBarrier' = [forceBarrier EXCEPT !.pass = TRUE]
       /\ pc' = [pc EXCEPT ![Master] = "AwaitWorkersComputation"]
       /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nWorkers, 
                       nProcess, bodiesComputed, workerCanStart, 
                       completedLatch, currentIteration, bodyIndex, bp >>

AwaitWorkersComputation == /\ pc[Master] = "AwaitWorkersComputation"
                           /\ completedLatch.counter = completedLatch.needed
                           /\ completedLatch' = [completedLatch EXCEPT !.counter = 0]
                           /\ pc' = [pc EXCEPT ![Master] = "ProcessLocal"]
                           /\ UNCHANGED << simulation, iterationsDone, bodies, 
                                           jobs, nWorkers, nProcess, 
                                           bodiesComputed, workerCanStart, 
                                           posBarrier, forceBarrier, 
                                           currentIteration, bodyIndex, bp >>

ProcessLocal == /\ pc[Master] = "ProcessLocal"
                /\ currentIteration' = currentIteration + 1
                /\ IF currentIteration' = simulation.iterations
                      THEN /\ simulation' = [simulation EXCEPT !.completed = TRUE]
                      ELSE /\ TRUE
                           /\ UNCHANGED simulation
                /\ posBarrier' = [posBarrier EXCEPT !.counter = posBarrier.counter + 1]
                /\ pc' = [pc EXCEPT ![Master] = "OkPos"]
                /\ UNCHANGED << iterationsDone, bodies, jobs, nWorkers, 
                                nProcess, bodiesComputed, workerCanStart, 
                                forceBarrier, completedLatch, bodyIndex, bp >>

OkPos == /\ pc[Master] = "OkPos"
         /\ posBarrier.counter = posBarrier.needed
         /\ pc' = [pc EXCEPT ![Master] = "rfb"]
         /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nWorkers, 
                         nProcess, bodiesComputed, workerCanStart, posBarrier, 
                         forceBarrier, completedLatch, currentIteration, 
                         bodyIndex, bp >>

rfb == /\ pc[Master] = "rfb"
       /\ forceBarrier' = [forceBarrier EXCEPT !.counter = 0]
       /\ pc' = [pc EXCEPT ![Master] = "rfp"]
       /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nWorkers, 
                       nProcess, bodiesComputed, workerCanStart, posBarrier, 
                       completedLatch, currentIteration, bodyIndex, bp >>

rfp == /\ pc[Master] = "rfp"
       /\ forceBarrier' = [forceBarrier EXCEPT !.pass = FALSE]
       /\ posBarrier' = [posBarrier EXCEPT !.pass = TRUE]
       /\ pc' = [pc EXCEPT ![Master] = "SimulationCicle"]
       /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nWorkers, 
                       nProcess, bodiesComputed, workerCanStart, 
                       completedLatch, currentIteration, bodyIndex, bp >>

ProcessSimulationEnd == /\ pc[Master] = "ProcessSimulationEnd"
                        /\ iterationsDone' = currentIteration
                        /\ pc' = [pc EXCEPT ![Master] = "Done"]
                        /\ UNCHANGED << simulation, bodies, jobs, nWorkers, 
                                        nProcess, bodiesComputed, 
                                        workerCanStart, posBarrier, 
                                        forceBarrier, completedLatch, 
                                        currentIteration, bodyIndex, bp >>

master == ScheduleWork \/ SimulationCicle \/ ForceBarrierReset \/ rpb
             \/ rpp \/ AwaitWorkersComputation \/ ProcessLocal \/ OkPos
             \/ rfb \/ rfp \/ ProcessSimulationEnd

WaitToStart(self) == /\ pc[self] = "WaitToStart"
                     /\ workerCanStart = TRUE
                     /\ pc' = [pc EXCEPT ![self] = "MainLoop"]
                     /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                     nWorkers, nProcess, bodiesComputed, 
                                     workerCanStart, posBarrier, forceBarrier, 
                                     completedLatch, currentIteration, 
                                     bodyIndex, bp >>

MainLoop(self) == /\ pc[self] = "MainLoop"
                  /\ IF simulation.completed = FALSE
                        THEN /\ pc' = [pc EXCEPT ![self] = "CalculateForceAndAcceleration"]
                        ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                  /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                  nWorkers, nProcess, bodiesComputed, 
                                  workerCanStart, posBarrier, forceBarrier, 
                                  completedLatch, currentIteration, bodyIndex, 
                                  bp >>

CalculateForceAndAcceleration(self) == /\ pc[self] = "CalculateForceAndAcceleration"
                                       /\ IF bodyIndex[self] <= Len(jobs[self])
                                             THEN /\ pc' = [pc EXCEPT ![self] = "BodyProcessForce"]
                                                  /\ UNCHANGED << forceBarrier, 
                                                                  bodyIndex >>
                                             ELSE /\ bodyIndex' = [bodyIndex EXCEPT ![self] = 1]
                                                  /\ forceBarrier' = [forceBarrier EXCEPT !.counter = forceBarrier.counter + 1]
                                                  /\ pc' = [pc EXCEPT ![self] = "AwaitForces"]
                                       /\ UNCHANGED << simulation, 
                                                       iterationsDone, bodies, 
                                                       jobs, nWorkers, 
                                                       nProcess, 
                                                       bodiesComputed, 
                                                       workerCanStart, 
                                                       posBarrier, 
                                                       completedLatch, 
                                                       currentIteration, bp >>

BodyProcessForce(self) == /\ pc[self] = "BodyProcessForce"
                          /\ bp' = [bp EXCEPT ![self] = bp[self] + jobs[self][bodyIndex[self]]]
                          /\ bodyIndex' = [bodyIndex EXCEPT ![self] = bodyIndex[self] + 1]
                          /\ pc' = [pc EXCEPT ![self] = "CalculateForceAndAcceleration"]
                          /\ UNCHANGED << simulation, iterationsDone, bodies, 
                                          jobs, nWorkers, nProcess, 
                                          bodiesComputed, workerCanStart, 
                                          posBarrier, forceBarrier, 
                                          completedLatch, currentIteration >>

AwaitForces(self) == /\ pc[self] = "AwaitForces"
                     /\ forceBarrier.pass = TRUE
                     /\ pc' = [pc EXCEPT ![self] = "CalculatePositions"]
                     /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                     nWorkers, nProcess, bodiesComputed, 
                                     workerCanStart, posBarrier, forceBarrier, 
                                     completedLatch, currentIteration, 
                                     bodyIndex, bp >>

CalculatePositions(self) == /\ pc[self] = "CalculatePositions"
                            /\ IF bodyIndex[self] <= Len(jobs[self])
                                  THEN /\ pc' = [pc EXCEPT ![self] = "BodyProcessPos"]
                                       /\ UNCHANGED bodyIndex
                                  ELSE /\ bodyIndex' = [bodyIndex EXCEPT ![self] = 1]
                                       /\ pc' = [pc EXCEPT ![self] = "NotifyMaster"]
                            /\ UNCHANGED << simulation, iterationsDone, bodies, 
                                            jobs, nWorkers, nProcess, 
                                            bodiesComputed, workerCanStart, 
                                            posBarrier, forceBarrier, 
                                            completedLatch, currentIteration, 
                                            bp >>

BodyProcessPos(self) == /\ pc[self] = "BodyProcessPos"
                        /\ bp' = [bp EXCEPT ![self] = bp[self] + jobs[self][bodyIndex[self]]]
                        /\ bodyIndex' = [bodyIndex EXCEPT ![self] = bodyIndex[self] + 1]
                        /\ pc' = [pc EXCEPT ![self] = "CalculatePositions"]
                        /\ UNCHANGED << simulation, iterationsDone, bodies, 
                                        jobs, nWorkers, nProcess, 
                                        bodiesComputed, workerCanStart, 
                                        posBarrier, forceBarrier, 
                                        completedLatch, currentIteration >>

NotifyMaster(self) == /\ pc[self] = "NotifyMaster"
                      /\ bodiesComputed' = bodiesComputed + bp[self]
                      /\ bp' = [bp EXCEPT ![self] = 0]
                      /\ completedLatch' = [completedLatch EXCEPT !.counter = completedLatch.counter + 1]
                      /\ posBarrier' = [posBarrier EXCEPT !.counter = posBarrier.counter + 1]
                      /\ pc' = [pc EXCEPT ![self] = "AwaitPosConsistency"]
                      /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                      nWorkers, nProcess, workerCanStart, 
                                      forceBarrier, currentIteration, 
                                      bodyIndex >>

AwaitPosConsistency(self) == /\ pc[self] = "AwaitPosConsistency"
                             /\ posBarrier.pass = TRUE
                             /\ pc' = [pc EXCEPT ![self] = "MainLoop"]
                             /\ UNCHANGED << simulation, iterationsDone, 
                                             bodies, jobs, nWorkers, nProcess, 
                                             bodiesComputed, workerCanStart, 
                                             posBarrier, forceBarrier, 
                                             completedLatch, currentIteration, 
                                             bodyIndex, bp >>

worker(self) == WaitToStart(self) \/ MainLoop(self)
                   \/ CalculateForceAndAcceleration(self)
                   \/ BodyProcessForce(self) \/ AwaitForces(self)
                   \/ CalculatePositions(self) \/ BodyProcessPos(self)
                   \/ NotifyMaster(self) \/ AwaitPosConsistency(self)

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == master
           \/ (\E self \in Workers: worker(self))
           \/ Terminating

Spec == /\ Init /\ [][Next]_vars
        /\ SF_vars(master)
        /\ \A self \in Workers : SF_vars(worker(self))

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION 

=============================================================================
\* Modification History
\* Last modified Wed Apr 06 18:30:19 CEST 2022 by andrea
\* Created Tue Apr 05 14:20:13 CEST 2022 by andrea
