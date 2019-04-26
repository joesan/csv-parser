package com.bigelectrons.joesan.csvparser

import java.nio.file.Paths

import org.joda.time.{DateTime, LocalTime}
import com.bigelectrons.joesan.csvparser.CsvParser.CsvRowParser._
import org.joda.time.format.DateTimeFormat
import org.scalatest.FlatSpec
import org.scalatest.concurrent.PatienceConfiguration.Interval

import scala.util.Try


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

  // For mapping 50_Hertz_Sekundarregelleistung_2015.csv
  case class SrlActivation(date: DateTime, start: LocalTime, end: LocalTime, positiveSRL: Double, negativeSRL: Double)

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

  "CSV Parser test" should "Parse CSV files and skip lines as we specify" in {
    // 50_Hertz_Sekundarregelleistung_2015.csv test (first 4 lines are unwanted, so we skip it)
    val srlCsvParserCfg = CSVParserConfig(
      withHeaders = true,
      caseClassCanonicalName = userCaseClass,
      skipLines = 4,
      separator = Semicolon
    )
    val srlCsv = s"$csvBasePath/50_Hertz_Sekundaerregelleistung_2015.csv"

    val srlParser = CsvParser.apply[SrlActivation]
    val srlSeq: Seq[SrlActivation] = srlParser.parse(srlCsv, srlCsvParserCfg)
    println(srlSeq.length)
    println(srlSeq.head)
    println(srlSeq.last)
    //srlSeq foreach println
  }
}
