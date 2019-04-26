package com.bigelectrons.joesan.csvparser

import shapeless.{Generic, HList}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}


object CsvParser {

  // This will be our interface between the Shapeless HList and our case class
  trait CsvRowParser[A] {
    // given a seq of String, we get back our case class parsed from the CSV
    def parse(row: Seq[String]): Try[A]
  }

  object CsvRowParser {
    implicit def all[A, H <: HList](implicit gen: Generic.Aux[A, H],
      read: CsvRowReader[H]): CsvRowParser[A] = (row: Seq[String]) => {
      read.from(row).map(gen.from)
    }
    def apply[A](implicit ev: CsvRowParser[A]): CsvRowParser[A] = ev
  }

  class CSVReader[A: CsvRowParser] {

    def parse(path: String, cfg: CSVParserConfig)(implicit m: scala.reflect.Manifest[A]): Seq[A] = ReaderWithFile[A](path).doParse(cfg)

    object ReaderWithFile {
      // This implicit conversion will be applied for cases where the caller does not
      // specify any parser configuration, so we resort to using a default which is a CSV file with
      // comma separated!
      implicit def parser2parsed[B](reader: ReaderWithFile[B])(implicit m: scala.reflect.Manifest[B]): Seq[B] = reader.doParse(defaultParserCfg)
    }

    case class ReaderWithFile[B: CsvRowParser : Manifest](path: String) {

      def doParse(cfg: CSVParserConfig)(implicit m: scala.reflect.Manifest[B]): Seq[B] = {

        def justSplit(line: String): Seq[String] = line.split(cfg.separator.separator).toList.collect {
          case elem if elem.nonEmpty => elem.trim
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
                  CsvRowParser[B].parse(elems) match {
                    case Success(suck) =>
                      parse(acc ++ Seq(suck), lines, lines.hasNext)
                    case Failure(ex) =>
                      println(s"some shit happened ${ex.getMessage}")
                      acc // currently ignoring the exceptions...
                  }
              }
            }
            else parse(acc, lines, lines.hasNext)
          }
          else acc
        }

        // The action starts here!
        Try(io.Source.fromFile(path)) match {
          case Success(bufferedSource) =>
            val lines = bufferedSource.getLines().drop(cfg.skipLines)
            implicit val headers: Seq[String] = lines.next.split(cfg.separator.separator).toList.map(_.trim)
            val elements = parse(Seq.empty[B], lines, lines.hasNext)
            // Let us not leak!
            bufferedSource.close()
            elements
          case Failure(fail) =>
            println(s"Reading from $path failed because of ${fail.getMessage}")
            Seq.empty[B]
        }
      }
    }
  }

  def apply[A: CsvRowParser]: CSVReader[A] = new CSVReader[A]
}
