package com.inland24.csvparser

import shapeless._

import scala.annotation.tailrec
import scala.io.Source
import scala.reflect.runtime.universe.TypeTag
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

  class CSVReader[A: CSVRowParser](implicit tag: TypeTag[A]) {
    def parse(path: String): ReaderWithFile[A] = ReaderWithFile[A](path)

    object ReaderWithFile {
      // This implicit conversion will be applied for cases where the caller does not
      // specify any parser configuration, so we resort to using a default which is a CSV file with
      // comma separated!
      implicit def parser2parsed[B](parse: ReaderWithFile[B]): Seq[B] = parse using CSVParserConfig(Comma)
    }

    // TODO: Recursively run through the lines and collect the errors if any!!
    case class ReaderWithFile[B: CSVRowParser](path: String) {
      def using(cfg: CSVParserConfig): Seq[B] = {
        val lines = Source.fromFile(path).getLines()

        // even before we could pass our shit into the CSVRowParser, let's have a sanity check!!
        println (tag.getClass.getCanonicalName)
        //println(implicitly[Typeable[B]].describe)

        @tailrec
        def tailRecursiveParse(acc: Seq[B], continue: Boolean): Seq[B] = {
          if (continue) {

            // we transform
            val splitted = lines.next.split(cfg.seperator.seperator).toList.map(_.trim)

            // obnoxious code to follow!
            val newAcc = CSVRowParser[B].parse(splitted) match {
              case Success(suck) =>
                acc ++ Seq(suck)
              case Failure(fcuk) =>
                println(s"unable to parse line $splitted << reason >> ${fcuk.getMessage}")
                fcuk.getStackTrace foreach println
                acc
            }
            // check if we have some more elements to parse
            if (lines.hasNext) tailRecursiveParse(newAcc, continue = true)
            else tailRecursiveParse(newAcc, continue = false)
          }
          else acc
        }

        tailRecursiveParse(Seq.empty[B], continue = true)
      }
    }
  }

  def apply[A: CSVRowParser: TypeTag] = new CSVReader[A]

  val reader = apply[Address]

  //val withDefaultCfg: Seq[Address] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/address.csv"
  //withDefaultCfg foreach println

  val withCustomConfig: Seq[Address] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/address.csv" using CSVParserConfig(Pipe)
  withCustomConfig foreach println
}