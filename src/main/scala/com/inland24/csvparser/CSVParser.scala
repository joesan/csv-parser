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

  class CSVReader[A: CSVParser.CSVRowParser] {
    def parse(path: String)(implicit m: scala.reflect.Manifest[A]): ReaderWithFile[A] = ReaderWithFile[A](Source.fromFile(path).getLines())
    def parse(lines: Iterator[String])(implicit m: scala.reflect.Manifest[A]): ReaderWithFile[A] = ReaderWithFile[A](lines)

    object ReaderWithFile {
      // This implicit conversion will be applied for cases where the caller does not
      // specify any parser configuration, so we resort to using a default which is a CSV file with
      // comma separated!
      implicit def parser2parsed[B](parse: ReaderWithFile[B])(implicit m: scala.reflect.Manifest[B]): Seq[B] = parse using CSVParserConfig(Comma)
    }

    // TODO: Recursively run through the lines and collect the errors if any!!
    case class ReaderWithFile[B: CSVRowParser : Manifest](lines: Iterator[String]) {
      def using(cfg: CSVParserConfig)(implicit m: scala.reflect.Manifest[B]): Seq[B] = {

        // let's set the implicit configurations in scope, these will be used by the CSVFieldReaders as needed
        implicit val seperator: Seperator = cfg.seperator
        if (lines.hasNext && cfg.withHeaders) {
          implicit val headers: Seq[String] = lines.next.split(cfg.seperator.seperator).toList.map(_.trim)
        }

        // even before we could pass our shit into the CSVRowParser, let's have a sanity check!!
        println (m.runtimeClass.getCanonicalName)
        //println(implicitly[Typeable[B]].describe)

        @tailrec
        def tailRecursiveParse(acc: Seq[B], lines: Iterator[String], hasMoreRows: Boolean): Seq[B] = {
          if (hasMoreRows) {
            // we split each row based on the seperator, collect all non empty rows trimming them on the way
            val splitted = lines.next.split(cfg.seperator.seperator).toSeq.collect {
              case elem if elem.nonEmpty => elem.trim
            }
            splitted match {
              // probably an empty row in the CSV, we just ignore it and proceed
              case Nil => tailRecursiveParse(acc, lines, hasMoreRows = lines.hasNext)
              case _   =>
                // we transform (TODO: this logic only applies to MeterData)
                // TODO:
                // using ClassTag feature, identify the target case class type and use
                // the splitting logic accordingly!!!
                println(splitted.head)
                println(splitted(1))
                val newSeq: Seq[String] = Seq(splitted.head, splitted(1), splitted.drop(2).mkString(cfg.seperator.seperator))

                // obnoxious code to follow!
                val newAcc = CSVRowParser[B].parse(newSeq) match {
                  case Success(suck) =>
                    acc ++ Seq(suck)
                  case Failure(fcuk) =>
                    println(s"unable to parse line because of ${fcuk.getMessage}")
                    //fcuk.getStackTrace foreach println
                    acc
                }
                // check if we have some more elements to parse
                tailRecursiveParse(newAcc, lines, hasMoreRows = lines.hasNext)
            }

          }
          else acc
        }

        tailRecursiveParse(Seq.empty[B], lines, hasMoreRows = lines.hasNext)
      }
    }
  }

  // TODO: remove this later...
  implicit val seperator: Seperator = Comma
  implicit val headers: Seq[String] = Seq("a", "b", "c")

  def apply[A: CSVRowParser] = new CSVReader[A]

  val reader = apply[MeterData]

  val withDefaultCfg1: Seq[MeterData] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/meter.csv"
  withDefaultCfg1 foreach println

  //val withDefaultCfg: Seq[Address] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/address.csv"
  //withDefaultCfg foreach println

  //val withCustomConfig: Seq[Address] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/address.csv" using CSVParserConfig(Pipe)
  //withCustomConfig foreach println
}