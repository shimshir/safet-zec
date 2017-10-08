name := "safet-zec"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "org.freemarker" % "freemarker" % "2.3.23",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.1",
  "io.spray" %% "spray-json" % "1.3.3",
  "org.reactivemongo" %% "reactivemongo" % "0.12.6",


  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.4" % "test"
)
