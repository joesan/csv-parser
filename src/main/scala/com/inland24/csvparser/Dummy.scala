package com.inland24.csvparser

import scala.io.Source

/**
  * Created by jothi on 05.05.17.
  */
object Dummy extends App {

  val lines = Source.fromFile("/Users/jothi/Projects/Private/scala-projects/csv-parser/meter.csv")

  val header = lines.getLines().take(1).toSeq
  val remaining = lines.getLines()

  val mappedHeader = header.flatMap (
    x => x.split(",")
  )


  while (remaining.hasNext) {
    val next = remaining.next()
    val splitted = next.split(",").toSeq
    val zipped = (mappedHeader zip splitted).tail.tail

    zipped

    zipped foreach println
    println("************")
  }
}
