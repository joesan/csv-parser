[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ae72f2c2cd3a414b9fe2f81e453749d8)](https://www.codacy.com/app/joesan/csv-parser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=joesan/csv-parser&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/joesan/csv-parser.svg?branch=master)](https://travis-ci.org/joesan/csv-parser)

# csv-parser

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ae72f2c2cd3a414b9fe2f81e453749d8)](https://www.codacy.com/app/joesan/csv-parser?utm_source=github.com&utm_medium=referral&utm_content=joesan/csv-parser&utm_campaign=badger)

A parser implementation for CSV files using the awesome Shapeless library

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 
See deployment for notes on how to deploy the project on a live system.

### Prerequisites

```
1. Install Java Version 8
2. Install Scala Version 2.11.8
3. Install IntelliJ - Latest community edition and then install the latest Scala plugin available
```

### Installing

Follow the steps below to import the project into IntelliJ

```
1. Clone the project from: 
   git clone https://github.com/joesan/csv-parser.git
   
2. Fire up IntelliJ and import the project
   
3. If you are opening IntelliJ for the first time, set up the Scala library in IntelliJ
```

### Running tests

You have the option here to either run tests from IntelliJ or from the command line

To run tests from the command line, do the following:

```
1. Open a terminal and navigate to the project root folder 
   
2. Issue the following command:
   sbt clean test
   
   TODO... document
```
To run any specific tests from within IntelliJ, simply right click the test that you wish you
run and click Run

### Running the application

This application is built as a standalone jar using sbt assembly. To run the application,
perform the following steps:

```
1. Open a terminal and navigate to the project root folder 
   
2. Issue the following command:
   sbt assembly
   
3. To run the jar file that was produced using the previous step: 
   java -cp target/scala-2.11/XXX.jar path.to.your.MainClass args(0)
   
   [TODO...] document!
```

## Deployment

Nothing to deploy! Just add this project as a dependency and you are done!

## Built With

* [SBT](http://www.scala-sbt.org/) - Scala Build Tool

## Contributing

[[TODO]] Add CONTRIBUTING.md to the project root

## Authors / Maintainers

* *Joesan*           - [Joesan @ GitHub](https://github.com/joesan/)

## License

Feel free to use it

## Acknowledgments

* To everybody that helped in this project
* The [Shapeless library](https://github.com/milessabin/shapeless)
