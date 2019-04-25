package com.bigelectrons.csvparser

import org.joda.time.DateTime
import com.bigelectrons.csvparser.CsvParser.CsvRowParser._
import org.scalatest.FlatSpec


class CSVParserTest extends FlatSpec {

  "CSV Parser test" should "Parse CSV files to Case classes" in {
    val canonicalName = Some(classOf[MeterData].getCanonicalName)
    val meterCsvParserCfg = CSVParserConfig(withHeaders = true, caseClassCanonicalName = canonicalName, splitterFn = Some(meterDataSplitter))
    val meterCsv = "/Users/joesan/Projects/Private/scala-projects/csv-parser/src/test/resources/meter.csv"
    val csvParser1 = CsvParser.apply[MeterData]
    val meterDataSeq: Seq[MeterData] = csvParser1.parse(meterCsv, meterCsvParserCfg)
    meterDataSeq foreach println
  }

  // this is our case class that we will parse into
  case class User(id: Int, firstName: String, lastName: String)
  case class Address(firstName: String, lastName: String, number: Int)

  // for... [TODO: use proper interval format]
  case class MeterData(meterId: String, dateTime: DateTime, meterReadings: Seq[Double])
  case class MeterDataAsMap(meterId: String, dateTime: DateTime, meterReadings: Map[String, Double])

  // Custom logic to split MeterData
  def meterDataSplitter(s: Seq[String]): Seq[String] = {
    Seq(s.head, s(1), s.drop(2).mkString(Comma.separator))
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
