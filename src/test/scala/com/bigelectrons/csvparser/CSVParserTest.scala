package com.bigelectrons.csvparser

import org.joda.time.DateTime
import com.bigelectrons.csvparser.CSVParser.CSVRowParser._
import org.scalatest.FlatSpec


class CSVParserTest extends FlatSpec {

  // TODO: We need this header to be resolved right here... otherwise it seems not to work! This is a dummy header just for testing!
  //implicit val headers: Seq[String] = Seq("a", "b", "c", "d")

  "CSV Parser test" should "Parse CSV files to Case classes" in {
    //val meterDataSeq: Seq[MeterData] = meterDataReader parse "/Users/joesan/Projects/Private/scala-projects/csv-parser/meter.csv" using (CSVParserConfig(withHeaders = true), fn = Some(meterDataSplitter))
    //meterDataSeq foreach println

    val meterDataMapSeq: Seq[MeterDataAsMap] = meterDataMapReader parse "/Users/joesan/Projects/Private/scala-projects/csv-parser/src/test/resources/meter.csv" using (CSVParserConfig(withHeaders = true))
    meterDataMapSeq foreach println

    //val result = CsvParser1[User].parseCSVFile("/Users/joesan/Projects/Private/scala-projects/csv-parser/user.csv")
    //result foreach println

    //val userSeq: Seq[User] = userReader parse "/Users/joesan/Projects/Private/scala-projects/csv-parser/user.csv" using CSVParserConfig(withHeaders = true)
    //userSeq foreach println
  }

  // this is our case class that we will parse into
  case class User(id: Int, firstName: String, lastName: String)
  case class Address(firstName: String, lastName: String, number: Int)

  // for... [TODO: use proper interval format]
  case class MeterData(meterId: String, dateTime: DateTime, meterReadings: Seq[Double])
  case class MeterDataAsMap(meterId: String, dateTime: DateTime, meterReadings: Map[String, Double])

  // TODO: We need this header to be resolved right here... otherwise it seems not to work! This is a dummy header just for testing!
  implicit val headers: Seq[String] = Seq("a", "b", "c", "d")

  val meterDataReader = CSVParser.apply[MeterData]
  val meterDataMapReader = CSVParser.apply[MeterDataAsMap]
  val userReader = CSVParser.apply[User]

  // Custom logic to split MeterData
  def meterDataSplitter(s: Seq[String]): Seq[String] = {
    Seq(s.head, s(1), s.drop(2).mkString(Comma.seperator))
  }



  //val withCustomConfig: Seq[Address] = reader parse "/Users/jothi/Projects/Private/scala-projects/csv-parser/address.csv" using CSVParserConfig(Pipe)
  //withCustomConfig foreach println
  /*
    readUsingAkkaStreams

    def readUsingAkkaStreams = {

      import java.io.File
      import akka.stream.scaladsl._
      import akka.actor.ActorSystem
      import akka.stream.ActorMaterializer
      import scala.concurrent.ExecutionContext.Implicits.global

      implicit val system = ActorSystem("Sys")
      implicit val materializer = ActorMaterializer()

      val file = new File("/Users/jothi/Projects/Private/scala-projects/csv-parser/meter.csv")

      val fileSource = FileIO.fromFile(file, 65536).via(Framing.delimiter(
        ByteString("\n"),
        maximumFrameLength = 65536,
        allowTruncation = true))

      val flow = fileSource.map(chunk => chunk.utf8String)

      val result = flow.runWith(Sink.foreach{
        case elem =>
          println("START: ******* ")
          println(elem)
          println("END: ********")
      })

      result.onComplete {
        case elem => elem match {
          case Success(_) => system.terminate()
          case Failure(fcku) => println(s"Something failed ${fcku.getMessage}")
        }
      }
    }  */
}