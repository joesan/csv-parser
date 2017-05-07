package com.inland24.csvparser

import org.joda.time.DateTime
import shapeless._

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Our main Parser class
  */
object CSVParser extends App {

  // This will be our interface between the Shapeless HList and our case class
  trait CSVRowParser[A] {
    // given a seq of String, we get back our case class parsed from the CSV
    def parse(row: Seq[String]): Try[A]
  }

  object CSVRowParser {

    implicit def all[A, H <: HList](implicit gen: Generic.Aux[A, H],
      read: CSVRowReader[H]): CSVRowParser[A] = new CSVRowParser[A] {
      def parse(row: Seq[String]): Try[A] = {
        read.from(row).map(gen.from)
      }
    }
    def apply[A](implicit ev: CSVRowParser[A]): CSVRowParser[A] = ev
  }

  // this is our case class that we will parse into
  case class User(id: Int, firstName: String, lastName: String)
  case class Address(firstName: String, lastName: String, number: Int)

  // for... [TODO: use proper interval format]
  case class MeterData(meterId: String, dateTime: DateTime, meterReadings: Seq[Double])
  case class MeterDataAsMap(meterId: String, dateTime: DateTime, meterReadings: Map[String, Double])

  class CSVReader[A: CSVParser.CSVRowParser] {
    def parse(path: String)(implicit m: scala.reflect.Manifest[A]): ReaderWithFile[A] = ReaderWithFile[A](Source.fromFile(path).getLines())
    def parse(lines: Iterator[String])(implicit m: scala.reflect.Manifest[A]): ReaderWithFile[A] = ReaderWithFile[A](lines)

    object ReaderWithFile {
      // This implicit conversion will be applied for cases where the caller does not
      // specify any parser configuration, so we resort to using a default which is a CSV file with
      // comma separated!
      implicit def parser2parsed[B](parse: ReaderWithFile[B])(implicit m: scala.reflect.Manifest[B]): Seq[B] = parse using CSVParserConfig(Comma)
    }

    // TODO: Collect the errors if any and if needed!!
    case class ReaderWithFile[B: CSVRowParser : Manifest](lines: Iterator[String]) {
      def using(cfg: CSVParserConfig)(implicit m: scala.reflect.Manifest[B]): Seq[B] = {

        // TODO: Where to put this stuff?? let's set the implicit configurations in scope, these will be used by the CSVFieldReaders as needed
        if (lines.hasNext && cfg.withHeaders) {
          println("this header information should be passed in to the CSVFieldReaders")
          implicit val headers: Seq[String] = lines.next.split(cfg.seperator.seperator).toList.map(_.trim)
        }

        def splitByRuntimeType(line: String): Seq[String] = {

          def justSplit: Seq[String] = line.split(cfg.seperator.seperator).toList.collect {
            case elem if elem.nonEmpty => elem.trim
          }

          // This is where you will add your new case classes, but if you think
          // adding stuff here might be nuisance, then we could pass a function
          // which will contain the split logic!
          m.runtimeClass.getCanonicalName match {
            case runtimeClass
              if runtimeClass == "com.inland24.csvparser.CSVParser.MeterData" || runtimeClass == "com.inland24.csvparser.CSVParser.MeterDataAsMap" =>
              val splitted = justSplit
              // we split as per our CSV data and in places where er mkString, we use a comma seperator
              Seq(splitted.head, splitted(1), splitted.drop(2).mkString(Comma.seperator))
            // the default way to split is to just split a line
            case _ =>
              justSplit
          }
        }

        @tailrec
        def parse(acc: Seq[B], lines: Iterator[String], hasMoreRows: Boolean): Seq[B] = {
          if (hasMoreRows) {
            val nextLine = lines.next
            // if we do not have anything in the current line, just skip and proceed
            if (nextLine.nonEmpty) {
              splitByRuntimeType(nextLine) match {
                case Nil   => parse(acc, lines, lines.hasNext)
                case elems =>
                  // obnoxious code to follow!
                  CSVRowParser[B].parse(elems) match {
                    case Success(suck) =>
                      parse(acc ++ Seq(suck), lines, lines.hasNext)
                    case Failure(ex) =>
                      println("some shit happened")
                      ex.printStackTrace()
                      acc // currently ignoring the exceptions...
                  }
              }
            }
            else parse(acc, lines, lines.hasNext)
          }
          else acc
        }
        parse(Seq.empty[B], lines, lines.hasNext)
      }
    }
  }

  def apply[A: CSVRowParser] = new CSVReader[A]

  // TODO: We need this header to be resolved right here... otherwise it seems not to work! This is a dummy header just for testing!
  implicit val headers: Seq[String] = Seq("a", "b", "c", "d")

  val meterDataReader = apply[MeterData]
  val meterDataMapReader = apply[MeterDataAsMap]
  val userReader = apply[User]

  val meterDataSeq: Seq[MeterData] = meterDataReader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/meter.csv" using CSVParserConfig(withHeaders = true)
  meterDataSeq foreach println

  val meterDataMapSeq: Seq[MeterDataAsMap] = meterDataMapReader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/meter.csv" using CSVParserConfig(withHeaders = true)
  meterDataMapSeq foreach println

  val userSeq: Seq[User] = userReader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/user.csv"
  userSeq foreach println

  //val withCustomConfig: Seq[Address] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/address.csv" using CSVParserConfig(Pipe)
  //withCustomConfig foreach println
}