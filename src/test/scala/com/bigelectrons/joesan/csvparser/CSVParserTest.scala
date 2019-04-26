package com.bigelectrons.joesan.csvparser

import java.nio.file.Paths

import org.joda.time.DateTime
import com.bigelectrons.joesan.csvparser.CsvParser.CsvRowParser._
import org.scalatest.FlatSpec


class CSVParserTest extends FlatSpec {

  // The base path where all the CSV files are located for unit testing purposes
  private val csvBasePath = Paths.get("src","test","resources").toAbsolutePath.toString

  // For mapping user.csv
  case class User(id: Int, name: String, age: Int, weight: Double)
  val userCaseClass = Some(classOf[User].getCanonicalName)

  // For mapping meter.csv
  case class MeterData(meterId: String, dateTime: DateTime, meterReadings: Seq[Double])
  // Custom logic to split MeterData row
  def meterDataSplitter(line: Seq[String]): Seq[String] = {
    Seq(line.head, line(1), line.drop(2).mkString(Comma.separator))
  }
  val meterDataCaseClass = Some(classOf[MeterData].getCanonicalName)

  // For mapping meter.csv
  // case class MeterDataAsMap(meterId: String, dateTime: DateTime, meterReadings: Map[String, Double])

  "CSV Parser test" should "Parse CSV files to their appropriate Case classes" in {
    // user.csv test
    val userCsvParserCfg = CSVParserConfig(withHeaders = true, caseClassCanonicalName = userCaseClass)
    val userCsv = s"$csvBasePath/user.csv"
    val userParser = CsvParser.apply[User]
    val userSeq: Seq[User] = userParser.parse(userCsv, userCsvParserCfg)
    userSeq foreach println

    // meter.csv test
    val meterCsvParserCfg = CSVParserConfig(withHeaders = true, caseClassCanonicalName = meterDataCaseClass, splitterFn = Some(meterDataSplitter))
    val meterCsv = s"$csvBasePath/meter.csv"
    val meterParser = CsvParser.apply[MeterData]
    val meterDataSeq: Seq[MeterData] = meterParser.parse(meterCsv, meterCsvParserCfg)
    meterDataSeq foreach println
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
