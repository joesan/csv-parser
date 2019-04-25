package com.bigelectrons


package object csvparser {

  sealed trait Separator { def separator: String }
  case object Comma extends Separator { val separator = "," }
  case object Semicolon extends Separator { val separator = ";" }
  case object Colon extends Separator { val separator = ":" }
  case object Pipe  extends Separator { val separator = "\\|" }
  case object Tilde extends Separator { val separator = "" }

  case class CSVParserConfig(
    separator: Separator = Comma,
    // Lines to skip until the header
    skipLines: Int = 0,
    caseClassCanonicalName: Option[String] = None,
    splitterFn: Option[Seq[String] => Seq[String]] = None,
    withErrors: Boolean = false,
    withHeaders: Boolean = false
  )

  val defaultParserCfg = CSVParserConfig()
}