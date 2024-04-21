# The Moirai Programming Language
Moirai is a programming language for server-based real-time computing (RTC). The worst-case execution time (WCET) is calculated for every script before it is executed. Moirai is ideal for multi-tenant microservices, serverless functions, and computed columns in databases. With a sufficiently pessimistic WCET limit, individual tenants will be unable to take down the server for all tenants.

* [Language Syntax Guide](https://github.com/moirai-lang/moirai-kt/wiki/Language-Syntax-Guide): A detailed overview of the language syntax.
* [Example Moirai Service](https://github.com/moirai-lang/moirai-service): An example webservice that can serve Moirai or JSON requests.
* [Using the Moirai Kotlin API](https://github.com/moirai-lang/moirai-kt/wiki/Using-the-Moirai-Kotlin-API): For if you want to import Moirai into your Kotlin or Java projects, for example if you are developing your own web service that will use Moirai.

# Language Use Cases
* Multi-tenant serverless or cloud compute services with no cold start time.
* Computed columns in a database
* Replace JSON requests when invoking web APIs.
* To very quickly update scripts in a live service without needing a full deployment.
* As a workflow language.
* As a config language.
* As a build tool language.

# Method for Calculating Worst-Case Execution Time
Every collection is dependently-typed on a pessimistic upper bound (called Fin). You can only loop if you have a collection. Moirai does not support the **while**, **do while**, or **for** loops. Recursion is impossible because all function invocations are topologically sorted.

Every node in the AST generates a **cost expression**, which is a deeply-nested operator tree, which applies the Max, Mul, and Sum operators to Fin type parameters. The cost expression is itself a type of AST, with its own interpreter. The cost expression for the entire program is executed and if the resulting value is too high, the program is rejected by the server before execution begins.

# Worst-Case Execution Time Examples
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
This code attempts to sneak past the total cost calculator by using higher-order functions. However, the compiler is smart enough to detect that this code will iterate 10,000 times, which is unacceptable. The computation is rejected and the server returns an error.

# For Computed Columns
Some database technologies allow users to define computed columns. These are virtual read-only columns that are calculated using other columns as input. Sometimes, SQL is used as the language to define these computed columns. In other cases, Java or Python are used. In all of these cases, the performance of the database may degrade if the computations are too complex.

Moirai makes an ideal alternative to using Turing-complete programming languages in computed columns. With a sufficiently pessimistic WCET limit, the database software can guarantee a minimum performance service-level agreement (SLA).

# As a Replacement for Data Transfer Formats (JSON/XML)
Arbitrary code sent over a network is also safe to execute. For this purpose, most existing products start with JSON and add Lisp-like features or embedded Abstract Syntax Trees.

```
// Trivial solution, JSON with an embedded Abstract Syntax Tree.

{ "op": "plus", "args": [ { "arg0": 5 }, { "arg1": 6 } ] }

// Slightly better solution, but strings need to be escaped. Nested code becomes unreadable.

{ "script": "(+ 5 6)" }

// Moirai code, sent over a network and directly executed by the server.

5 + 6
```

Moirai is _not_ a JSON generator. Moirai replaces JSON completely. At no point is JSON sent over the network, and at no point does the destination server deserialize Moirai into another language. The request sent over the network and the cloud function code are both written in Moirai.

In the [example webservice](https://github.com/moirai-lang/moirai-service), JSON _can_ be sent over the network. However, the Moirai interpreter does not know what JSON is, and it does not add any JSON parsers as a dependency. Only the webservice code adds JSON dependencies, and JSON is only used to generate a Moirai script. This option is provided by the example service in case the user does not feel comfortable executing arbitrary code.

# Etymology
Moirai is a term from ancient Greek mythology. It refers the three sisters who personify destiny.