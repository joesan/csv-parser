package com.inland24.csvparser

import com.inland24.csvparser.CSVParser.MeterData
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, PeriodFormatter, PeriodFormatterBuilder}

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

  implicit def dateTimeCSVConverter: CSVFieldReader[DateTime] = new CSVFieldReader[DateTime] {
    def from(s: String): Try[DateTime] = Try {
      DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(s)
    }
  }

  implicit def seqCSVFieldConverter(implicit seperator: Seperator): CSVFieldReader[Seq[Double]] = new CSVFieldReader[Seq[Double]] {
    def from(s: String): Try[Seq[Double]] = Try {
      val seq = s.split(seperator.seperator).toSeq
      seq.drop(2).map(_.toDouble)
    }
  }

  implicit def mapCSVFieldConverter(implicit headers: Seq[String], seperator: Seperator): CSVFieldReader[Map[String, Double]] = new CSVFieldReader[Map[String, Double]] {
    def from(s: String): Try[Map[String, Double]] = Try {
      val seq = s.split(seperator.seperator).toSeq
      (headers.drop(2) zip seq.drop(2).map(_.toDouble)).toMap
    }
  }

  implicit def meterDataCSVFieldConverter(implicit headers: Seq[String], seperator: Seperator): CSVFieldReader[MeterData] = new CSVFieldReader[MeterData] {
    def from(s: String): Try[MeterData] = Try {
      val seq = s.split(seperator.seperator).toSeq
      MeterData(seq.head, new DateTime(seq(1)), seq.drop(2).map(_.toDouble))
    }
  }
}