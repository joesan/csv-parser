package com.inland24.csvparser

import com.opencsv.{ CSVReader => OpenCSVReader }
import shapeless._

import scala.io.Source
import scala.util.Try

/**
  * Our main Parser class
  */
object CSVParser extends App {

  // This will be our interface between the Shapeless HList and our case class
  trait CSVRowParser[A] {
    def parse[H <: HList](row: List[String])
     (implicit gen: Generic.Aux[A, H], read: CSVRowReader[H]): Try[A] = {
       // did you ever see it uglier than this?
       // all I'm doing here is read the CSV row which is a List[String] using the implicitly
       // provided Reader instance which in this case is the CSVRowReader[H] (actually an instance of it)
       // and map that to my case class type supplied via the Generic interface from the Shapeless library
       read from row map gen.from
    }
  }

  // this is our case class that we will parse into
  case class User(id: Int, firstName: String, lastName: String)

  def csvRowParser[A] = new CSVRowParser[A] {}

  class CSVReader[A] {

    def parse(path: String): ReaderWithFile[A] = ReaderWithFile[A](path)

    object ReaderWithFile {
      implicit def parser2parsed[B](parser: ReaderWithFile[B]): Seq[B] = parser.using(defaultParserCfg)
    }

    case class ReaderWithFile[B](path: String) {

      // import the CSVRowReaders in scope
      import CSVRowReader._

      def using(cfg: CSVParserConfig): Seq[B] = {
        val lines = Source.fromFile(path).getLines()
        while(lines.hasNext) {
          // we transform
          val splitted = lines.next.split(cfg.seperator.seperator).toList
          val line = csvRowParser[B].parse(splitted)
          println(line)
        }
        println(cfg)   // line 2
        null
      }
    }
  }

  def apply[A] = new CSVReader[A]

  val reader = apply[User]

  val openCSVReader = new OpenCSVReader(new java.io.FileReader("/Users/jothi/Projects/Private/scala-projects/csv-parser/test.csv"))

  import scala.collection.JavaConverters._
  openCSVReader.readAll.asScala.foreach {
    case elem => {
      val user = csvRowParser[User].parse(elem.toList)
    }
  }

  //val withDefaultCfg: Seq[User] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/test.csv"

  // but this works
  val withCustomCfg = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/test.csv" using CSVParserConfig(Pipe)

}