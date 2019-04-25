package com.bigelectrons.csvparser

import shapeless._

import scala.annotation.tailrec
import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success, Try}

/**
  * Our main Parser class
  */
object DummyParser extends App {

  // This will be our interface between the Shapeless HList and our case class
  trait CSVRowParser[A] {
    // given a seq of String, we get back our case class parsed from the CSV
    def parse(row: Seq[String]): Try[A]
  }

  object CSVRowParser {

    implicit def all[A, H <: HList](implicit gen: Generic.Aux[A, H],
      read: CsvRowReader[H]): CSVRowParser[A] = (row: Seq[String]) => {
      read.from(row).map(gen.from)
    }
    def apply[A](implicit ev: CSVRowParser[A]): CSVRowParser[A] = ev
  }

  class CSVReader[A: CSVRowParser] {
    def using[A, B <: {def close(): Unit}] (closeable: B) (f: B => A): A =
      try { f(closeable) } finally { closeable.close }

    /*def parse(path: String)(implicit m: scala.reflect.Manifest[A]): ReaderWithFile[A] = {
      Try(Source.fromFile(path)) match {
        case Success(source) =>
          val reader = ReaderWithFile[A](source.getLines())
          source.close()
          reader
        case Failure(fail) =>
          println(s"Reading CSV file failed because of ${fail.getMessage}")
          ReaderWithFile[A](Iterator.empty)
      }
    }*/
    def parse1(path: String)(implicit m: scala.reflect.Manifest[A]): ReaderWithFile[A] = ReaderWithFile[A](Source.fromFile(path).getLines())
    def parse(path: String)(implicit m: scala.reflect.Manifest[A]): ReaderWithFile[A] = {
      using(io.Source.fromFile(path)) { source =>
        ReaderWithFile[A](source.getLines())
      }
    }

    def parse(lines: Iterator[String])(implicit m: scala.reflect.Manifest[A]): ReaderWithFile[A] = ReaderWithFile[A](lines)

    object ReaderWithFile1 {
      // This implicit conversion will be applied for cases where the caller does not
      // specify any parser configuration, so we resort to using a default which is a CSV file with
      // comma separated!
      implicit def parser2parsed[B](parse: ReaderWithFile[B])(implicit m: scala.reflect.Manifest[B]): Seq[B] = parse using defaultParserCfg
    }

    // TODO: Collect the errors if any and if needed!! Handling Errors is not yet implemented!!!!
    case class ReaderWithFile[B: CSVRowParser : Manifest](lines: Iterator[String]) {
      def using(cfg: CSVParserConfig)(implicit m: scala.reflect.Manifest[B]): Seq[B] = {

        def justSplit(line: String): Seq[String] = line.split(cfg.separator.separator).toList.collect {
          case elem if elem.nonEmpty => elem.trim
        }

        // TODO: Where to put this stuff?? let's set the implicit configurations in scope, these will be used by the CSVFieldReaders as needed
        if (lines.hasNext && cfg.withHeaders) {
          println("this header information should be passed in to the CSVFieldReaders")
          implicit val headers: Seq[String] = lines.next.split(cfg.separator.separator).toList.map(_.trim)
        }

        def splitByRuntimeType(line: String): Seq[String] = {
          // This is where you will add your new case classes, but if you think
          // adding stuff here might be nuisance, then we could pass a function
          // which will contain the split logic!
          m.runtimeClass.getCanonicalName match {
            case runtimeClass
              if cfg.caseClassCanonicalName.isDefined &&
                cfg.splitterFn.isDefined && runtimeClass == cfg.caseClassCanonicalName.get =>
              // we split as per our CSV data and in places where we mkString, we use a comma separator
              cfg.splitterFn.get(justSplit(line))
            // the default way to split is to just split a line by the separator in the CSV file
            case _ =>
              justSplit(line)
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
                      println(s"some shit happened ${ex.getMessage}")
                      ex.printStackTrace()
                      acc // currently ignoring the exceptions...
                  }
              }
            }
            else parse(acc, lines, lines.hasNext)
          }
          else acc
        }

        // Here we start with an empty line and recursively parse the CSV file
        parse(Seq.empty[B], lines, lines.hasNext)
      }
    }
  }

  def apply[A: CSVRowParser] = new CSVReader[A]
}