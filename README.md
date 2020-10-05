# The ShardScript Programming Language
This package contains the code for an interpreter for a programming language called ShardScript. ShardScript is a Turing-incomplete replacement for JSON. Unlike JSON, which can only represent data, ShardScript can represent both data and code.

ShardScript is optimized for arbitrary cross-network code injections between machines. For this purpose, most existing products start with JSON and add Lisp-like features. By contrast, ShardScript starts with C and subtracts anything that is not safe. The result is a more powerful and terse language that is easier to write by-hand.

The absolute upper limit to the execution cost of an AST must be determinable at compile-time, so collection types have an additional type parameter called Omicron which represents the maximum allowed capacity even if the collection is modified at runtime.

Because ShardScript cannot access the file system of the host machine, and because the cost-to-execute is known at compile time, arbitrary cross-network code injections are generally safe.

# Sub-Packages

ShardScript consists of 4 packages as well as an acceptance test suite. The packages must be built in the order they appear below.

## shardscript-semantics

Abstract Syntax Tree (AST) and Semantic Analysis (SA) classes for the ShardScript programming language

## shardscript-grammar

The lexer and parser, implemented using the ANTLR domain-specific programming language/parser generator

## shardscript-composition

Compiler frontend, contains ANTLR parse tree visitors and import management

## shardscript-eval

Includes an AST visitor that can map an AST to a Value

## shardscript-acceptance

Full suite of acceptance tests for the ShardScript language