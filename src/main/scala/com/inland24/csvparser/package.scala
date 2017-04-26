package com.inland24


package object csvparser {

  sealed trait Seperator { def seperator: String }
  case object Comma extends Seperator { val seperator = "," }
  case object Pipe  extends Seperator { val seperator = "|" }
  case object Tilde extends Seperator { val seperator = "" }

  case class CSVParserConfig(seperator: Seperator = Comma)

   val defaultParserCfg = CSVParserConfig()
}
