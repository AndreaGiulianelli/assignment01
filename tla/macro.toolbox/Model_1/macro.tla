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
    nProcess = Cardinality(Workers) + 1,
    
    posBarrier = [counter |-> 0, needed |-> nProcess, exiting |-> 0];
    barries = [pos |-> posBarrier]
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
                    skip;
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
    begin MainLoop:
        while simulation.completed = FALSE do
            AwaitPosConsistency:
                skip;
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

\* BEGIN TRANSLATION (chksum(pcal) = "8020d03f" /\ chksum(tla) = "da0b025e")
VARIABLES simulation, iterationsDone, bodies, jobs, nProcess, posBarrier, 
          barries, pc

(* define statement *)
MasterDoAllIteration == <>[](iterationsDone = simulation.iterations)

VARIABLE currentIteration

vars == << simulation, iterationsDone, bodies, jobs, nProcess, posBarrier, 
           barries, pc, currentIteration >>

ProcSet == {Master} \cup (Workers)

Init == (* Global variables *)
        /\ simulation = [iterations |-> 2, nBodies |-> 4, completed |-> FALSE]
        /\ iterationsDone = 0
        /\ bodies \in PT!TupleOf({1}, simulation.nBodies)
        /\ jobs = [w \in Workers |-> <<>>]
        /\ nProcess = Cardinality(Workers) + 1
        /\ posBarrier = [counter |-> 0, needed |-> nProcess, exiting |-> 0]
        /\ barries = [pos |-> posBarrier]
        (* Process master *)
        /\ currentIteration = 0
        /\ pc = [self \in ProcSet |-> CASE self = Master -> "ScheduleWork"
                                        [] self \in Workers -> "MainLoop"]

ScheduleWork == /\ pc[Master] = "ScheduleWork"
                /\ TRUE
                /\ pc' = [pc EXCEPT ![Master] = "SimulationCicle"]
                /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                nProcess, posBarrier, barries, 
                                currentIteration >>

SimulationCicle == /\ pc[Master] = "SimulationCicle"
                   /\ IF currentIteration < simulation.iterations
                         THEN /\ pc' = [pc EXCEPT ![Master] = "OkPos"]
                         ELSE /\ pc' = [pc EXCEPT ![Master] = "Process"]
                   /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                   nProcess, posBarrier, barries, 
                                   currentIteration >>

OkPos == /\ pc[Master] = "OkPos"
         /\ TRUE
         /\ pc' = [pc EXCEPT ![Master] = "AwaitComputation"]
         /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, nProcess, 
                         posBarrier, barries, currentIteration >>

AwaitComputation == /\ pc[Master] = "AwaitComputation"
                    /\ TRUE
                    /\ pc' = [pc EXCEPT ![Master] = "ProcessLocal"]
                    /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                    nProcess, posBarrier, barries, 
                                    currentIteration >>

ProcessLocal == /\ pc[Master] = "ProcessLocal"
                /\ currentIteration' = currentIteration + 1
                /\ pc' = [pc EXCEPT ![Master] = "SimulationCicle"]
                /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                nProcess, posBarrier, barries >>

Process == /\ pc[Master] = "Process"
           /\ iterationsDone' = currentIteration
           /\ simulation' = [simulation EXCEPT !.completed = TRUE]
           /\ pc' = [pc EXCEPT ![Master] = "Done"]
           /\ UNCHANGED << bodies, jobs, nProcess, posBarrier, barries, 
                           currentIteration >>

master == ScheduleWork \/ SimulationCicle \/ OkPos \/ AwaitComputation
             \/ ProcessLocal \/ Process

MainLoop(self) == /\ pc[self] = "MainLoop"
                  /\ IF simulation.completed = FALSE
                        THEN /\ pc' = [pc EXCEPT ![self] = "AwaitPosConsistency"]
                        ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                  /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                  nProcess, posBarrier, barries, 
                                  currentIteration >>

AwaitPosConsistency(self) == /\ pc[self] = "AwaitPosConsistency"
                             /\ TRUE
                             /\ pc' = [pc EXCEPT ![self] = "CalculateForceAndAcceleration"]
                             /\ UNCHANGED << simulation, iterationsDone, 
                                             bodies, jobs, nProcess, 
                                             posBarrier, barries, 
                                             currentIteration >>

CalculateForceAndAcceleration(self) == /\ pc[self] = "CalculateForceAndAcceleration"
                                       /\ TRUE
                                       /\ pc' = [pc EXCEPT ![self] = "AwaitForces"]
                                       /\ UNCHANGED << simulation, 
                                                       iterationsDone, bodies, 
                                                       jobs, nProcess, 
                                                       posBarrier, barries, 
                                                       currentIteration >>

AwaitForces(self) == /\ pc[self] = "AwaitForces"
                     /\ TRUE
                     /\ pc' = [pc EXCEPT ![self] = "CalculatePositions"]
                     /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                     nProcess, posBarrier, barries, 
                                     currentIteration >>

CalculatePositions(self) == /\ pc[self] = "CalculatePositions"
                            /\ TRUE
                            /\ pc' = [pc EXCEPT ![self] = "NotifyMaster"]
                            /\ UNCHANGED << simulation, iterationsDone, bodies, 
                                            jobs, nProcess, posBarrier, 
                                            barries, currentIteration >>

NotifyMaster(self) == /\ pc[self] = "NotifyMaster"
                      /\ TRUE
                      /\ pc' = [pc EXCEPT ![self] = "MainLoop"]
                      /\ UNCHANGED << simulation, iterationsDone, bodies, jobs, 
                                      nProcess, posBarrier, barries, 
                                      currentIteration >>

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
\* Last modified Wed Apr 06 00:32:24 CEST 2022 by andrea
\* Created Tue Apr 05 14:20:13 CEST 2022 by andrea
