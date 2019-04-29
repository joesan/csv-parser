[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ae72f2c2cd3a414b9fe2f81e453749d8)](https://www.codacy.com/app/joesan/csv-parser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=joesan/csv-parser&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/joesan/csv-parser.svg?branch=master)](https://travis-ci.org/joesan/csv-parser)
[![Open Source](https://img.shields.io/badge/Open%20Source-100%25-yellowgreen.svg)]()

# csv-parser [![Unit Tests](https://img.shields.io/badge/unit--tests-0%25-red.svg)]()
A parser implementation for CSV files using the awesome Shapeless library! Just parse any CSV file of your choice.

All you have to specify is a case class that you want the CSV to parse into and of course the CSV file that you want to parse. With this information, the library can produce a Sequence of your case classes. Any errors are being logged and possibly ignored!

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Pre-requisites

```
1. Install Java Version 8
2. Install Scala Version 2.12.7
3. Install SBT version 1.2.8
4. Install IntelliJ - Latest community edition and then install the latest Scala / SBT plugins available
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
```

To run any specific tests from within IntelliJ, simply right click the test that you wish you
run and click Run

### Using the parser

This application is built as a standalone jar and published to the OSS Sonatype repos. To add the parser
as a dependency to your project's build.sbt:

```
libraryDependencies += "com.bigelectrons.csvparser" %% "csv-parser" % version
```

Latest `version`: [![Latest version](https://index.scala-lang.org/bigelectrons/csv-parser/csv-parser/latest.svg)](https://index.scala-lang.org/bigelectrons/csv-parser/csv-parser)

TODO: Documentation pending: 

Once added as a dependency, you can use the parser as below:

    case class MeterData(meterId: String, dateTime: DateTime, meterReadings: Seq[Double])
    
    val canonicalName = Some(classOf[MeterData].getCanonicalName)
    val meterCsvParserCfg = CSVParserConfig(withHeaders = true, caseClassCanonicalName = canonicalName, splitterFn = Some(meterDataSplitter))
    val meterCsv = "/path/to/csv/file/meterdata.csv"
    val csvParser = CsvParser.apply[MeterData]
    val meterDataSeq: Seq[MeterData] = csvParser.parse(meterCsv, meterCsvParserCfg)

## Built With

* [SBT](http://www.scala-sbt.org/) - Scala Build Tool

## Authors / Maintainers

* *Joesan*           - [Joesan @ GitHub](https://github.com/joesan/)

## License

Feel free to use it

## Acknowledgments

* To everybody that helped in this project
* The [Shapeless library](https://github.com/milessabin/shapeless)
