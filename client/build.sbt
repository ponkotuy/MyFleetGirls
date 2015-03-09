import AssemblyKeys._

// put this at the top of the file

name := "MyFleetGirls"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.twitter" %% "finagle-http" % "6.24.0",
  "com.netaporter" %% "scala-uri" % "0.4.4",
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.apache.httpcomponents" % "httpclient" % "4.4",
  "org.apache.httpcomponents" % "httpmime" % "4.4"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")

assemblySettings

mainClass in assembly := Some("com.ponkotuy.run.Main")

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "com.ponkotuy.build"
