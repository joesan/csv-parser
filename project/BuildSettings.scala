import sbt._
import Keys._

object BuildSettings {

  lazy val basicSettings = Seq[Setting[_]](
    organization  := "com.inland24.csvparser",
    version       := "0.1.0-SNAPAHOT",
    description   := "standalone csv parser",
    scalaVersion  := "2.11.7",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    resolvers     ++= Dependencies.resolutionRepos
  )

  // sbt-assembly settings for building onefat jar
  import sbtassembly.Plugin._
  import AssemblyKeys._
  lazy val sbtAssemblySettings = assemblySettings ++ Seq(

    jarName in assembly := {
      name.value + "-" + version.value + ".jar"
    },

    // META-INF discarding
    mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
       {
        case PathList("META-INF", xs @ _*) => MergeStrategy.discard
        case x => MergeStrategy.first
       }
    }
  )

  lazy val buildSettings = basicSettings ++ sbtAssemblySettings
}
