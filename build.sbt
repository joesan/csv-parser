scalacOptions += "-Ypartial-unification"

name := "csv-parser"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

lazy val csvparser = project.in(file(".")).
  settings(
    name := "csv-parser",
    organization := "com.bigelectrons.joesan",
    description := "Generic CSV parser library using Shapeless scala library",
    scalaVersion := "2.12.4",
    normalizedName := "csv-parser",
    crossScalaVersions := Seq("2.10.6", "2.11.11, 2.12.2"),
    homepage := Some(url("https://github.com/joesan/csv-parser")),
    licenses += ("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/joesan/csv-parser"),
      "scm:git:git@github.com/joesan/csv-parser.git",
      Some("scm:git:git@github.com/joesan/csv-parser.git"))),
    publishMavenStyle := true,
    isSnapshot := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra :=
      <developers>
      <developer>
        <id>joesan</id>
        <name>Joesan</name>
        <url>https://github.com/joesan</url>
      </developer>
    </developers>,
    pomIncludeRepository := { _ => false }
  )

useGpg := true
publishMavenStyle := true
credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credential")

logBuffered in Test := false

libraryDependencies ++= Seq(
  "joda-time" %  "joda-time" % "2.7",
  "com.chuusai" %% "shapeless" % "2.3.3",
  "com.opencsv" %  "opencsv" % "3.9",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
