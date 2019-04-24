package com.bigelectrons


package object csvparser {

  sealed trait Seperator { def seperator: String }
  case object Comma extends Seperator { val seperator = "," }
  case object Semicolon extends Seperator { val seperator = ";" }
  case object Colon extends Seperator { val seperator = ":" }
  case object Pipe  extends Seperator { val seperator = "\\|" }
  case object Tilde extends Seperator { val seperator = "" }

  case class CSVParserConfig(
    seperator: Seperator = Comma,
    skipLines: Int = 0, // How many lines from the first couple of lines to skip
    withErrors: Boolean = false,
    withHeaders: Boolean = false
  )

  val defaultParserCfg = CSVParserConfig()
}