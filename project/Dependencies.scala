import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Spray Repository" at "http://repo.spray.cc/"
  )

  // versions for all the used libraries
   object Versions {
    val jodaTime            = "2.7"
    val log4j               = "1.2.14"
    val shapeless           = "2.3.2"
    val openCSV             = "3.9"
    val scalaReflect        = "2.11.8"
  }

  object Libraries {
    val jodaTime             = "joda-time"          %  "joda-time"                 % Versions.jodaTime
    val log4j                = "log4j"              %  "log4j"                     % Versions.log4j
    val openCSV              = "com.opencsv"        %  "opencsv"                   % Versions.openCSV
    val shapeless            = "com.chuusai"        %% "shapeless"                 % Versions.shapeless
    val scalaReflect         = "org.scala-lang"     %% "scala-reflect"             % Versions.scalaReflect
  }
}
