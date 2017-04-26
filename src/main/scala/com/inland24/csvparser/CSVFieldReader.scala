package com.inland24.csvparser

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
}