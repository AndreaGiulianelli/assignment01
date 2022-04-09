# Algoritmo macro version 2
**Algoritmo associato alla Petri Net di nome: macro_version2.ndr**

Supponiamo un sistema in cui siano presenti 5 worker e il master.

## Common
```
nWorkers = 5
posB = Barrier(nWorkers + 1)
forceB = Barrier(nWorkers)
completitionL = Latch(nWorkers)
```

## Master
```
foreach worker
    m0: createAndStartWorker()
end foreach

loop:
    m1: posB.hitAndWait()
    m2: completitionL.await()
    m3: getResult()
```

## Worker
```
w0: awaitToBeStarted()
loop:
    w1: posB.hitAndWait()
    w2: calculateForce()
    w3: forceB.hitAndWait()
    w4: calculatePos()
    w5: completitionL.countDown()
``` 

## Verifiche
```
[](-(M5/\(W3\/W6)))
[](-(W3/\W6))
[](<>W1)
[](W1=>(<>W7))
[](M3=>(<>M2))
```

## Note
Al fine di poter descrivere le barriere attraverso le reti di petri ho provveduto a scomporla in due azioni atomiche hit and wait, controllate però da **barrier count** e da **barrier handler** in modo tale da non avere "race condition" nella rete di petri.