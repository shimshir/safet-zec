name := "safet-zec"

version := "1.0"

scalaVersion := "2.12.3"

resolvers += "repo.jenkins-ci.org" at "http://repo.jenkins-ci.org/public"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.19",
  "org.freemarker" % "freemarker" % "2.3.23",
  "com.github.jknack" % "handlebars" % "4.0.6",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.1",
  "io.spray" %% "spray-json" % "1.3.3",
  "org.reactivemongo" %% "reactivemongo" % "0.12.6",
  "org.kohsuke" % "github-api" % "1.89",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "commons-io" % "commons-io" % "1.4" % Provided,

  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.4",

  "org.mockito" % "mockito-core" % "2.8.47" % Test,
  "org.scalatest" %% "scalatest" % "3.0.4" % Test

)

fork in Test := true
