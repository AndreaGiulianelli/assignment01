------------------------------- MODULE macro -------------------------------

EXTENDS TLC, Sequences, Integers, FiniteSets
PT == INSTANCE PT
CONSTANTS Workers, Master

(*--algorithm macro

variables
    simulation = [iterations |-> 2, nBodies |-> 4, completed |-> FALSE],
    iterationsDone = 0,
    bodies \in PT!TupleOf({1}, simulation.nBodies),
    jobs = [w \in Workers |-> <<>>],
    nWorkers = Cardinality(Workers),
    nProcess = nWorkers + 1,
    bodiesComputed = 0,
    
    workerCanStart = FALSE,
    posBarrier = [counter |-> 0, needed |-> nProcess, pass |-> FALSE],
    forceBarrier = [counter |-> 0, needed |-> nWorkers, pass |-> FALSE],
    completedLatch = [counter |-> 0, needed |-> nWorkers, pass |-> FALSE];
define
    \* Liveness properties
    MasterDoAllIterations == <>[](iterationsDone = simulation.iterations)
    AllBodiesComputed == <>[](bodiesComputed = (simulation.nBodies * simulation.iterations * 2))
    \* Safety invariant
    SafetyInUpdate == ((\E w \in Workers: pc[w] = "CalculateForceAndAcceleration") /\ (\E w \in Workers: pc[w] = "CalculatePositions")) = FALSE
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
            while currentIteration < simulation.iterations do
                ForceReset:
                    await forceBarrier.counter = forceBarrier.needed;
                    rpb: posBarrier.counter := 0; \* reset pos barrier counter
                    rpp: posBarrier.pass := FALSE; \* reset pos barrier pass
                    forceBarrier.pass := TRUE;
                AwaitComputation:
                    await completedLatch.counter = completedLatch.needed;
                    completedLatch.counter := 0;
                ProcessLocal:
                    currentIteration := currentIteration + 1;
                    if currentIteration = simulation.iterations then
                        simulation.completed := TRUE;
                    end if;
                \* Put the pos barrier in the end in order to avoid deadlock of the worker on the last iteration
                posBarrier.counter := posBarrier.counter + 1;
                OkPos:
                    await posBarrier.counter = posBarrier.needed;
                    rfb: forceBarrier.counter := 0; \* reset force barrier counter
                    rfp: forceBarrier.pass := FALSE; \* reset force barrier pass
                    posBarrier.pass := TRUE;
            end while;
        Process:
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
            \* Put the pos barrier in the end in order to avoid deadlock of the worker on the last iteration
            posBarrier.counter := posBarrier.counter + 1;
            AwaitPosConsistency:
                await posBarrier.pass = TRUE;
        end while;
end process;



end algorithm
*)

\* BEGIN TRANSLATION (chksum(pcal) = "cfe57466" /\ chksum(tla) = "1c6292a3")
VARIABLES simulation, iterationsDone, bodies, jobs, nWorkers, nProcess, 
          bodiesComputed, workerCanStart, posBarrier, forceBarrier, 
          completedLatch, pc

(* define statement *)
MasterDoAllIterations == <>[](iterationsDone = simulation.iterations)
AllBodiesComputed == <>[](bodiesComputed = (simulation.nBodies * simulation.iterations * 2))

SafetyInUpdate == ((\E w \in Workers: pc[w] = "CalculateForceAndAcceleration") /\ (\E w \in Workers: pc[w] = "CalculatePositions")) = FALSE

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
        /\ completedLatch = [counter |-> 0, needed |-> nWorkers, pass |-> FALSE]
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
                   /\ IF currentIteration < simulation.iterations
                         THEN /\ pc' = [pc EXCEPT ![Master] = "ForceReset"]
                         ELSE /\ pc' = [pc EXCEPT ![Master] = "Process"]
                   /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                   nWorkers, nProcess, bodiesComputed, 
                                   workerCanStart, posBarrier, forceBarrier, 
                                   completedLatch, currentIteration, bodyIndex, 
                                   bp >>

ForceReset == /\ pc[Master] = "ForceReset"
              /\ forceBarrier.counter = forceBarrier.needed
              /\ pc' = [pc EXCEPT ![Master] = "rpb"]
              /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                              nWorkers, nProcess, bodiesComputed, 
                              workerCanStart, posBarrier, forceBarrier, 
                              completedLatch, currentIteration, bodyIndex, bp >>

rpb == /\ pc[Master] = "rpb"
       /\ posBarrier' = [posBarrier EXCEPT !.counter = 0]
       /\ pc' = [pc EXCEPT ![Master] = "rpp"]
       /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nWorkers, 
                       nProcess, bodiesComputed, workerCanStart, forceBarrier, 
                       completedLatch, currentIteration, bodyIndex, bp >>

rpp == /\ pc[Master] = "rpp"
       /\ posBarrier' = [posBarrier EXCEPT !.pass = FALSE]
       /\ forceBarrier' = [forceBarrier EXCEPT !.pass = TRUE]
       /\ pc' = [pc EXCEPT ![Master] = "AwaitComputation"]
       /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nWorkers, 
                       nProcess, bodiesComputed, workerCanStart, 
                       completedLatch, currentIteration, bodyIndex, bp >>

AwaitComputation == /\ pc[Master] = "AwaitComputation"
                    /\ completedLatch.counter = completedLatch.needed
                    /\ completedLatch' = [completedLatch EXCEPT !.counter = 0]
                    /\ pc' = [pc EXCEPT ![Master] = "ProcessLocal"]
                    /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                    nWorkers, nProcess, bodiesComputed, 
                                    workerCanStart, posBarrier, forceBarrier, 
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

Process == /\ pc[Master] = "Process"
           /\ iterationsDone' = currentIteration
           /\ pc' = [pc EXCEPT ![Master] = "Done"]
           /\ UNCHANGED << simulation, bodies, jobs, nWorkers, nProcess, 
                           bodiesComputed, workerCanStart, posBarrier, 
                           forceBarrier, completedLatch, currentIteration, 
                           bodyIndex, bp >>

master == ScheduleWork \/ SimulationCicle \/ ForceReset \/ rpb \/ rpp
             \/ AwaitComputation \/ ProcessLocal \/ OkPos \/ rfb \/ rfp
             \/ Process

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
\* Last modified Wed Apr 06 16:01:34 CEST 2022 by andrea
\* Created Tue Apr 05 14:20:13 CEST 2022 by andrea
