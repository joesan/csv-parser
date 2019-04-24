scalacOptions += "-Ypartial-unification"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

logBuffered in Test := false

libraryDependencies ++= Seq(
  "joda-time" %  "joda-time" % "2.7",
  "com.chuusai" %% "shapeless" % "2.3.3",
  "com.opencsv" %  "opencsv" % "3.9",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
