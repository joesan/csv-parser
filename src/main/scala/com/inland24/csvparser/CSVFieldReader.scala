package com.inland24.csvparser

import org.joda.time.format.{PeriodFormatter, PeriodFormatterBuilder}

import scala.util.{Success, Try}

/**
  * Contains the Readers for the primitives
  */
trait CSVFieldReader[A] {

  def from(s: String): Try[A]
}
object CSVFieldReader {

  def apply[A](implicit reader: CSVFieldReader[A]): CSVFieldReader[A] = reader

  implicit def stringCSVFieldConverter: CSVFieldReader[String] = new CSVFieldReader[String] {
    def from(s: String): Try[String] = Success(s)
  }

  implicit def intCSVFieldConverter: CSVFieldReader[Int] = new CSVFieldReader[Int] {
    def from(s: String): Try[Int] = Try(s.toInt)
  }

  implicit def doubleCSVFieldConverter: CSVFieldReader[Double] = new CSVFieldReader[Double] {
    def from(s: String): Try[Double] = Try(s.toDouble)
  }

  implicit def booleanCSVFieldConverter: CSVFieldReader[Boolean] = new CSVFieldReader[Boolean] {
    def from(s: String): Try[Boolean] = s.toLowerCase match {
      case "yes" | "1" => Success(true)
      case "no"  | "0" => Success(false)
      case _           => Success(false) // I do not understand what else it could be, so falsify everything else!!
    }
  }

  sealed trait TimeSeperator { def seperator: String }
  case object ColonTimeSeperator extends TimeSeperator { val seperator = ":" }
  case object HyphenTimeSeperator extends TimeSeperator { val seperator = "-" }

  def timeHHmmCSVFieldConverter(t: TimeSeperator): CSVFieldReader[PeriodFormatter] = new CSVFieldReader[PeriodFormatter] {
    def from(s: String): Try[PeriodFormatter] = Try {
      new PeriodFormatterBuilder()
        .appendHours()
        .appendSeparator(t.seperator)
        .appendMinutes()
        .toFormatter
    }
  }

  implicit def timeHHmmWithColonSeperator: CSVFieldReader[PeriodFormatter] = timeHHmmCSVFieldConverter(ColonTimeSeperator)
  implicit def timeHHmmWithHyphenSeperator: CSVFieldReader[PeriodFormatter] = timeHHmmCSVFieldConverter(HyphenTimeSeperator)
}