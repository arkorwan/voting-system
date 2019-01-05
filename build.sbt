import sbt.Keys.version

name := "voting-system"

val versions = Map(
  "databob" -> "1.6.0",
  "mockito" -> "2.7.22",
  "scalaMock" -> "3.2",
  "scalaTest" -> "2.2.0",
  "typesafeConfig" -> "1.3.1",
  "enumeratum" -> "1.5.13",
  "monocle" -> "1.5.0",
  "kantan.csv" -> "0.5.0"
)

organization := "io.github.arkorwan"
version := "0.1"
scalaVersion := "2.11.8"
scalacOptions += "-deprecation"
updateOptions := updateOptions.value.withCachedResolution(true)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % versions("typesafeConfig"),
  "com.beachape" %% "enumeratum" % versions("enumeratum"),
  "com.github.julien-truffaut" %% "monocle-core" % versions("monocle"),
  "com.github.julien-truffaut" %% "monocle-macro" % versions("monocle"),
  "com.nrinaudo" %% "kantan.csv" % versions("kantan.csv"),
  "com.nrinaudo" %% "kantan.csv-generic" % versions("kantan.csv"),
  // testing
  "org.mockito" % "mockito-core" % versions("mockito") % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % versions("scalaMock") % "test",
  "org.scalatest" %% "scalatest" % versions("scalaTest") % "test",
  "io.github.daviddenton" %% "databob" % versions("databob") % "test"
)

