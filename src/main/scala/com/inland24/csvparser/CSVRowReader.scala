package com.inland24.csvparser

import shapeless._

import scala.util.{Failure, Success, Try}

/**
  * Contains the Reader for reading a row in a CSV file
  */
trait CSVRowReader[H <: HList] {

  def from(row: Seq[String]): Try[H]
}
object CSVRowReader {
  import HList.ListCompat._

  // if you do not know what these two methods mean, have a look at the Shapeless Guide
  def apply[H <: HList](implicit materializer: CSVRowReader[H]): CSVRowReader[H] = materializer

  def instance[H <: HList](f: Seq[String] => Try[H]) = new CSVRowReader[H] {
    def from(row: Seq[String]): Try[H] = f(row)
  }

  // we now need converters to convert the List[String] in each row to a shapeless HList and HNil
  implicit def hNilFromCSVRow: CSVRowReader[HNil] = instance {
    case Nil => Success(HNil)
    case _   => Failure(new RuntimeException("some shit happened when transforming to case class"))
  }

  implicit def hConsFromCSVRow[HEAD: CSVFieldReader, TAIL <: HList: CSVRowReader]: CSVRowReader[HEAD :: TAIL] =
    instance {
      case h :: t => for {
        head <- CSVFieldReader[HEAD].from(h)
        tail <- CSVRowReader[TAIL].from(t)
      } yield head :: tail
      case Nil => Failure(new RuntimeException("Expected more fields"))
  }
}