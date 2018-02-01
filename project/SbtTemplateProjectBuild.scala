import sbt.{addCompilerPlugin, _}
import Keys._

object SbtTemplateProjectBuild extends Build {

  import Dependencies._
  import BuildSettings._

  // Configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  // Define our project, with basic project information and library dependencies
  lazy val project = Project("csv-parser", file("."))
    .settings(buildSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        Libraries.log4j,
        Libraries.jodaTime,
        Libraries.shapeless,
        Libraries.openCSV,
        Libraries.akkaStreams
        //Libraries.scalaReflect
        // Add your additional libraries here (comma-separated)...
      )
    )
  .settings(
    resolvers += Resolver.bintrayRepo("tek", "maven"),
    addCompilerPlugin("tryp" %% "splain" % "0.1.22")
  )
}