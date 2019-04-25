ThisBuild / organization := "com.bigelectrons.csvparser"
ThisBuild / organizationName := "bigelectrons"
ThisBuild / organizationHomepage := Some(url("https://www.bigelectrons.com/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/joesan/csv-parser"),
    "scm:git@github.com:joesan/csv-parser.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "joesan",
    name  = "Joesan",
    email = "secret@@email",
    url   = url("https://www.bigelectrons.com")
  )
)

ThisBuild / description := "Generic CSV parser library using Shapeless scala library"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/joesan/csv-parser"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
