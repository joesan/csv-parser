package com.bigelectrons.joesan.csvparser

import org.joda.time.{DateTime, LocalTime}
import org.joda.time.format.DateTimeFormat

import scala.util.{Success, Try}

/**
  * Contains the Readers for the primitives
  */
trait CsvFieldReader[A] {

  def from(s: String): Try[A]
}
object CsvFieldReader {

  // All supported DateFormat's, please add for any additional formats needed!
  private val dateTimePatterns = Seq(
    DateTimeFormat.forPattern("dd/MM/yyyy"),
    DateTimeFormat.forPattern("dd.MM.yyyy")
  )
  // All supported Hour formats, please add for any additional formats needed!
  private val hourMinutesPattern = Seq(DateTimeFormat.forPattern("HH:mm"))

  def apply[A](implicit reader: CsvFieldReader[A]): CsvFieldReader[A] = reader

  implicit def stringCSVFieldConverter: CsvFieldReader[String] = (s: String) => Success(s)

  implicit def intCSVFieldConverter: CsvFieldReader[Int] = (s: String) => Try(s.toInt)

  implicit def doubleCSVFieldConverter: CsvFieldReader[Double] = (s: String) => Try(s.toDouble)

  implicit def booleanCSVFieldConverter: CsvFieldReader[Boolean] = (s: String) => s.toLowerCase match {
    case "yes" | "1" => Success(true)
    case "no" | "0" => Success(false)
    case _ => Success(false) // I do not understand what else it could be, so falsify everything else!!
  }

  implicit def hhMMCSVConverter: CsvFieldReader[LocalTime] = (s: String) => Try {
    val (successes, failures) = hourMinutesPattern.map(fmt => Try(fmt.parseLocalTime(s))).partition(_.isSuccess)
    if (successes.nonEmpty)
      successes.head.get
    else
      failures.head.get
  }

  implicit def dateTimeCSVConverter: CsvFieldReader[DateTime] = (s: String) => Try {
    val (successes, failures) = dateTimePatterns.map(fmt => Try(fmt.parseDateTime(s))).partition(_.isSuccess)
    if (successes.nonEmpty)
      successes.head.get
    else
      failures.head.get
  }

  implicit def seqCSVFieldConverter: CsvFieldReader[Seq[Double]] = (s: String) => Try {
    s.split(Comma.separator).map(_.toDouble).toSeq
  }

  // TODO: this is not yet used...
  implicit def mapCSVFieldConverter(implicit headers: Seq[String]): CsvFieldReader[Map[String, Double]] = (s: String) => Try {
    val seq = s.split(Comma.separator).map(_.toDouble).toSeq
    (headers zip seq).toMap
  }
}