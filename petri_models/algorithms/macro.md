# Algoritmo macro
**Algoritmo associato alla Petri Net di nome: macro.ndr**

Supponiamo un sistema in cui siano presenti 3 Worker + il Master
## Common
```
nWorkers = 3
nBody = 6
l = Latch(nWorkers)
```

## Master
```

    foreach worker
m0: createAndStartWorker
    end foreach

loop:
        foreach body
    m1: createForceTask()
        end foreach
    m2: await(l)
        foreach body
    m3: createPosTask()
        end foreach
    m4: await(l)

```

## Worker
```
loop:
    w0: awaitToBeStart()
    w1: getTask()
    w2: executeTask()
    w3: notify(l)
```

## Verifiche
Sul foglio, ma comunque le principali:
```
[](-(M3/\M6));
[](<>W2);
[](W2=>(<>W3));
[]((M5=>(<>M6))/\(M8=>(<>M3)));
```

## Note
Ho esplicitato i workers e i bodies mettendo rispettivamente 3 e 6 token, anche se in realtà non vi era bisogno.
Quindi forse le istruzioni dentro i foreach non sono completamente rappresentate dalla Rete di Petri, è stato astratto dal relativo ciclo. Questo l'ho fatto visto che è una rete di Petri di Analisi e quindi meglio lavorare ad alto livello.

### Motivazione
All'inizio avevo simulato anche i foreach attraverso la rete di petri solo che non ero riuscito (avevo creato deadlock o reti non bounded).