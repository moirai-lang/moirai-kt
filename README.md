# The ShardScript Programming Language
ShardScript is designed for arbitrary cross-network code injections between machines. For this purpose, most existing products start with JSON and add Lisp-like features or embedded Abstract Syntax Trees.

```
// Naive solution, JSON with an embedded Abstract Syntax Tree.

{ "op": "plus", "args": [ { "arg0": 5 }, { "arg1": 6 } ] }

// Slightly better solution, but strings need to be escaped. Nested code becomes unreadable.

{ "script": "(+ 5 6)" }

// ShardScript code, sent over a network and directly executed by the server.

5 + 6
```

The absolute upper limit to the execution cost of ShardScript code is determinable at compile-time. The Halting Problem is solved per-request. To accomplish this, all collections are dependently-typed on a pessimistic upper bound, called Fin. Because ShardScript cannot access the file system of the host machine, and because the cost-to-execute is known at compile time, arbitrary cross-network code injections are generally safe.

ShardScript is not a JSON generator. ShardScript replaces JSON completely. At no point is JSON sent over the network, and at no point does the destination server deserialize ShardScript into another language. The request sent over the network and the cloud function code are both written in ShardScript.

# Concept Examples
The following code can be sent over a network in a POST request to be executed by a server:
```
def maxOf<O: Fin>(list: List<Int, O>): Int {
     mutable max = 0
     for(item in list) {
         if(item > max) {
             max = item
         }
     }
     max
 }
 
 val list = List(4, 7, 2, 1, 9, 8)
 maxOf(list)
```
This code defines a function called maxOf, which accepts a list of unknown size. This function is then called. The server is able to run this code safely because it can infer that the type parameter named O needs to be substituted with the integer constant 6. This means that the list will only ever have 6 elements, allowing the for loop to iterate only 6 times. The compiler will be OK with this computation, and return a result to the sender.

By contrast, the compiler will reject the following computation:
```
def f(g: (Int, Int) -> Int): Int {
    val x = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    for(c in x) {
        for(d in x) {
            g(c, d)
        }
    }
    x[0]
}

f(lambda (c: Int, d: Int) -> {
    val x = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    for(a in x) {
        for(b in x) {
            a + b + c + d
        }
    }
    x[0]
})
```
This code attempts to sneak past the total cost calculator by using higher-order functions. However, the compiler is smart enough to detect that this code will iterate 10,000 times, which is unacceptable. The computation is rejcted and the server returns an error.

# Etymology
The word "shard" in ShardScript comes from Massively Multiplayer Online games in the 1990s. Sharding is a server programming technique that allows several players to inhabit the same location, fighting the same enemies and collecting the same resources, without seeing or interacting with each other.

The metaphor here is that ShardScript is well-suited for multi-tenant environments where the different tenants can execute their code independently on the same hardware.
