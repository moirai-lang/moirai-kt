# The Moirai Programming Language
The Moirai programming language is designed for multi-tenant serverless cloud compute, but it can also be used to eliminate Data Transfer Formats (such as JSON or XML) as a method of communication between computers. This repository contains the Kotlin implementation of the Moirai interpreter.

The best place to find information about the language is on the Wiki.

* [Introduction to the Moirai Programming Language](https://github.com/moirai-lang/moirai-kt/wiki/Introduction-to-the-Moirai-Programming-Language): An overview of the purpose of the Moirai Programming Language.
* [Language Syntax Guide](https://github.com/moirai-lang/moirai-kt/wiki/Language-Syntax-Guide): A detailed overview of the language syntax.
* [Using the Moirai Kotlin API](https://github.com/moirai-lang/moirai-kt/wiki/Using-the-Moirai-Kotlin-API): For if you want to import Moirai into your Kotlin or Java projects, for example if you are developing your own web service that will use Moirai.

# Getting Started
Clone this repository, and then open the resulting folder in IntelliJ IDEA. After IDEA has finished loading the project, click the Gradle icon (it looks like an elephant) on the far right bar, below the notifications bell.

Open moirai/Tasks/other and right-click compileTestKotlin. Click "Run" and wait for the Gradle task to complete. Then, right-click the test/kotlin folder in the project view on the left. Click "Run Tests". On the master branch, all tests should succeed.

There is currently no REPL. The fastest way to write Moirai code is to add new tests.