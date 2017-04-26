package com.inland24.csvparser

import com.opencsv.CSVReader

/**
  * Created by jothi on 26.04.17.
  */
object Dummy extends App {


  import scala.util.{ Failure, Success, Try }

  trait Read[A] { def reads(s: String): Try[A] }

  object Read {
    def apply[A](implicit readA: Read[A]): Read[A] = readA

    implicit object stringRead extends Read[String] {
      def reads(s: String): Try[String] = Success(s)
    }

    implicit object intRead extends Read[Int] {
      def reads(s: String) = Try(s.toInt)
    }

    // And so on...
  }

  import shapeless._

  trait FromRow[L <: HList] { def apply(row: List[String]): Try[L] }

  object FromRow {
    import HList.ListCompat._

    def apply[L <: HList](implicit fromRow: FromRow[L]): FromRow[L] = fromRow

    def fromFunc[L <: HList](f: List[String] => Try[L]) = new FromRow[L] {
      def apply(row: List[String]): Try[L] = f(row)
    }

    implicit val hnilFromRow: FromRow[HNil] = fromFunc {
      case Nil => Success(HNil)
      case _ => Failure(new RuntimeException("No more rows expected"))
    }

    implicit def hconsFromRow[H: Read, T <: HList: FromRow]: FromRow[H :: T] =
      fromFunc {
        case h :: t => for {
          hv <- Read[H].reads(h)
          tv <- FromRow[T].apply(t)
        } yield hv :: tv
        case Nil => Failure(new RuntimeException("Expected more cells"))
      }
  }

  trait RowParser[A] {
    def apply[L <: HList](row: List[String])(implicit
                                             gen: Generic.Aux[A, L],
                                             fromRow: FromRow[L]
    ): Try[A] = fromRow(row).map(gen. from)
  }

  def rowParserFor[A] = new RowParser[A] {}

  import scala.collection.JavaConverters._

  case class User(id: Int, firstName: String, lastName: String)

  val reader = new CSVReader(new java.io.FileReader("/Users/jothi/Projects/Private/scala-projects/csv-parser/test.csv"))

  val users = reader.readAll.asScala.map(row => rowParserFor[User](row.toList))

  println(users)

}
