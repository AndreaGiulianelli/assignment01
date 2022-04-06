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
    
    
    bodiesComputed = 0,
    completedLatch = [counter |-> 0, needed |-> Cardinality(Workers)];
    posBarrier = 0;
    okPosBarrier = FALSE;
define
    MasterDoAllIteration == <>[](iterationsDone = simulation.iterations)
end define




fair+ process master = Master
    variables    
        currentIteration = 0;
    begin
        ScheduleWork:
            skip;
        SimulationCicle:
            while currentIteration < simulation.iterations do
                OkPos:
                  await posBarrier = nWorkers;
                AwaitComputation:
                    skip;
                ProcessLocal:
                    currentIteration := currentIteration + 1;
            end while;
        Process:
            iterationsDone := currentIteration;
            simulation.completed := TRUE;
end process;

fair+ process worker \in Workers
    variables
        bodyIndex = 1;
    begin MainLoop:
        while simulation.completed = FALSE do
            posBarrier := posBarrier + 1;
            AwaitPosConsistency:
                await posBarrier = nWorkers;
            CalculateForceAndAcceleration:
                skip;
            AwaitForces:
                skip;
            CalculatePositions:
                skip;
            NotifyMaster:
                skip;
        end while;
end process;



end algorithm
*)

\* BEGIN TRANSLATION (chksum(pcal) = "7d4e1c8e" /\ chksum(tla) = "432ed4a5")
VARIABLES simulation, iterationsDone, bodies, jobs, nWorkers, bodiesComputed, 
          completedLatch, posBarrier, okPosBarrier, pc

(* define statement *)
MasterDoAllIteration == <>[](iterationsDone = simulation.iterations)

VARIABLES currentIteration, bodyIndex

vars == << simulation, iterationsDone, bodies, jobs, nWorkers, bodiesComputed, 
           completedLatch, posBarrier, okPosBarrier, pc, currentIteration, 
           bodyIndex >>

ProcSet == {Master} \cup (Workers)

Init == (* Global variables *)
        /\ simulation = [iterations |-> 2, nBodies |-> 4, completed |-> FALSE]
        /\ iterationsDone = 0
        /\ bodies \in PT!TupleOf({1}, simulation.nBodies)
        /\ jobs = [w \in Workers |-> <<>>]
        /\ nWorkers = Cardinality(Workers)
        /\ bodiesComputed = 0
        /\ completedLatch = [counter |-> 0, needed |-> Cardinality(Workers)]
        /\ posBarrier = 0
        /\ okPosBarrier = FALSE
        (* Process master *)
        /\ currentIteration = 0
        (* Process worker *)
        /\ bodyIndex = [self \in Workers |-> 1]
        /\ pc = [self \in ProcSet |-> CASE self = Master -> "ScheduleWork"
                                        [] self \in Workers -> "MainLoop"]

ScheduleWork == /\ pc[Master] = "ScheduleWork"
                /\ TRUE
                /\ pc' = [pc EXCEPT ![Master] = "SimulationCicle"]
                /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                nWorkers, bodiesComputed, completedLatch, 
                                posBarrier, okPosBarrier, currentIteration, 
                                bodyIndex >>

SimulationCicle == /\ pc[Master] = "SimulationCicle"
                   /\ IF currentIteration < simulation.iterations
                         THEN /\ pc' = [pc EXCEPT ![Master] = "OkPos"]
                         ELSE /\ pc' = [pc EXCEPT ![Master] = "Process"]
                   /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                   nWorkers, bodiesComputed, completedLatch, 
                                   posBarrier, okPosBarrier, currentIteration, 
                                   bodyIndex >>

OkPos == /\ pc[Master] = "OkPos"
         /\ posBarrier = nWorkers
         /\ pc' = [pc EXCEPT ![Master] = "AwaitComputation"]
         /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nWorkers, 
                         bodiesComputed, completedLatch, posBarrier, 
                         okPosBarrier, currentIteration, bodyIndex >>

AwaitComputation == /\ pc[Master] = "AwaitComputation"
                    /\ TRUE
                    /\ pc' = [pc EXCEPT ![Master] = "ProcessLocal"]
                    /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                    nWorkers, bodiesComputed, completedLatch, 
                                    posBarrier, okPosBarrier, currentIteration, 
                                    bodyIndex >>

ProcessLocal == /\ pc[Master] = "ProcessLocal"
                /\ currentIteration' = currentIteration + 1
                /\ pc' = [pc EXCEPT ![Master] = "SimulationCicle"]
                /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                nWorkers, bodiesComputed, completedLatch, 
                                posBarrier, okPosBarrier, bodyIndex >>

Process == /\ pc[Master] = "Process"
           /\ iterationsDone' = currentIteration
           /\ simulation' = [simulation EXCEPT !.completed = TRUE]
           /\ pc' = [pc EXCEPT ![Master] = "Done"]
           /\ UNCHANGED << bodies, jobs, nWorkers, bodiesComputed, 
                           completedLatch, posBarrier, okPosBarrier, 
                           currentIteration, bodyIndex >>

master == ScheduleWork \/ SimulationCicle \/ OkPos \/ AwaitComputation
             \/ ProcessLocal \/ Process

MainLoop(self) == /\ pc[self] = "MainLoop"
                  /\ IF simulation.completed = FALSE
                        THEN /\ posBarrier' = posBarrier + 1
                             /\ pc' = [pc EXCEPT ![self] = "AwaitPosConsistency"]
                        ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                             /\ UNCHANGED posBarrier
                  /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                  nWorkers, bodiesComputed, completedLatch, 
                                  okPosBarrier, currentIteration, bodyIndex >>

AwaitPosConsistency(self) == /\ pc[self] = "AwaitPosConsistency"
                             /\ posBarrier = nWorkers
                             /\ pc' = [pc EXCEPT ![self] = "CalculateForceAndAcceleration"]
                             /\ UNCHANGED << simulation, iterationsDone, 
                                             bodies, jobs, nWorkers, 
                                             bodiesComputed, completedLatch, 
                                             posBarrier, okPosBarrier, 
                                             currentIteration, bodyIndex >>

CalculateForceAndAcceleration(self) == /\ pc[self] = "CalculateForceAndAcceleration"
                                       /\ TRUE
                                       /\ pc' = [pc EXCEPT ![self] = "AwaitForces"]
                                       /\ UNCHANGED << simulation, 
                                                       iterationsDone, bodies, 
                                                       jobs, nWorkers, 
                                                       bodiesComputed, 
                                                       completedLatch, 
                                                       posBarrier, 
                                                       okPosBarrier, 
                                                       currentIteration, 
                                                       bodyIndex >>

AwaitForces(self) == /\ pc[self] = "AwaitForces"
                     /\ TRUE
                     /\ pc' = [pc EXCEPT ![self] = "CalculatePositions"]
                     /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                     nWorkers, bodiesComputed, completedLatch, 
                                     posBarrier, okPosBarrier, 
                                     currentIteration, bodyIndex >>

CalculatePositions(self) == /\ pc[self] = "CalculatePositions"
                            /\ TRUE
                            /\ pc' = [pc EXCEPT ![self] = "NotifyMaster"]
                            /\ UNCHANGED << simulation, iterationsDone, bodies, 
                                            jobs, nWorkers, bodiesComputed, 
                                            completedLatch, posBarrier, 
                                            okPosBarrier, currentIteration, 
                                            bodyIndex >>

NotifyMaster(self) == /\ pc[self] = "NotifyMaster"
                      /\ TRUE
                      /\ pc' = [pc EXCEPT ![self] = "MainLoop"]
                      /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                      nWorkers, bodiesComputed, completedLatch, 
                                      posBarrier, okPosBarrier, 
                                      currentIteration, bodyIndex >>

worker(self) == MainLoop(self) \/ AwaitPosConsistency(self)
                   \/ CalculateForceAndAcceleration(self)
                   \/ AwaitForces(self) \/ CalculatePositions(self)
                   \/ NotifyMaster(self)

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
\* Last modified Tue Apr 05 23:35:25 CEST 2022 by andrea
\* Created Tue Apr 05 14:20:13 CEST 2022 by andrea
