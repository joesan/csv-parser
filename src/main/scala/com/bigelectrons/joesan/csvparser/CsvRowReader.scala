package com.bigelectrons.joesan.csvparser

import shapeless._

import scala.util.{Failure, Success, Try}

/**
  * Contains the Reader for reading a row in a CSV file
  */
trait CsvRowReader[H <: HList] {

  def from(row: Seq[String]): Try[H]
}
object CsvRowReader {
  import HList.ListCompat._

  // if you do not know what these two methods mean, have a look at the Shapeless Guide
  def apply[H <: HList](implicit materializer: CsvRowReader[H]): CsvRowReader[H] = materializer

  def instance[H <: HList](f: Seq[String] => Try[H]) = new CsvRowReader[H] {
    def from(row: Seq[String]): Try[H] = f(row)
  }

  // we now need converters to convert the List[String] in each row to a shapeless HList and HNil
  implicit def hNilFromCSVRow: CsvRowReader[HNil] = instance {
    case Nil => Success(HNil)
    case _  => Failure(new RuntimeException("some shit happened when transforming to case class"))
  }

  implicit def hConsFromCSVRow[H: CsvFieldReader, T <: HList: CsvRowReader]: CsvRowReader[H :: T] =
    instance {
      case h :: t => for {
        head <- CsvFieldReader[H].from(h)
        tail <- CsvRowReader[T].from(t)
      } yield head :: tail
      case Nil => Failure(new Exception("Expected more fields"))
    }
}