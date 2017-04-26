package com.inland24.csvparser

import shapeless._

import scala.io.Source
import scala.util.Try

/**
  * Our main Parser class
  */
object CSVParser extends App {

  // This will be our interface between the Shapeless HList and our case class
  trait CSVRowParser[A] {
    def parse(row: List[String]): Try[A]
  }

  object CSVRowParser {
    implicit def all[A, R <: HList](implicit gen: Generic.Aux[A, R],
                                    read: CSVRowReader[R]): CSVRowParser[A] = new CSVRowParser[A] {
      def parse(row: List[String]): Try[A] = {
        read.from(row).map(gen.from)
      }
    }
    def apply[A](implicit ev: CSVRowParser[A]): CSVRowParser[A] = ev
  }

  // this is our case class that we will parse into
  case class User(id: Int, firstName: String, lastName: String)

  class CSVReader[A: CSVRowParser] {
    val defaultParserCfg = CSVParserConfig(Comma)

    def parse(path: String): ReaderWithFile[A] = ReaderWithFile[A](path)

    object ReaderWithFile {
      implicit def parser2parsed[B](parser: ReaderWithFile[B]): Seq[B] = parser.using(defaultParserCfg)
    }

    case class ReaderWithFile[B: CSVRowParser](path: String) {
      def using(cfg: CSVParserConfig): Seq[B] = {
        val lines = Source.fromFile(path).getLines()
        while (lines.hasNext) {
          // we transform
          val splitted = lines.next.split(cfg.seperator.seperator).toList
          val line = CSVRowParser[B].parse(splitted)
          println(line)
        }
        println(cfg) // line 2
        null
      }
    }
  }

  def apply[A: CSVRowParser] = new CSVReader[A]

  val reader = apply[User]

  val withDefaultCfg: Seq[User] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/test.csv"
  val withCustomConfig: Seq[User] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/test.csv" using CSVParserConfig(Pipe)
}