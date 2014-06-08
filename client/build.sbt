import AssemblyKeys._

// put this at the top of the file

name := "MyFleetGirls"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.twitter" %% "finagle-http" % "6.16.0",
  "com.github.theon" %% "scala-uri" % "0.3.6", // 0.4.1はScala2.11専用
  "org.json4s" %% "json4s-native" % "3.2.9",
  "org.apache.httpcomponents" % "httpclient" % "4.3.3",
  "org.apache.httpcomponents" % "httpmime" % "4.3.3"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")

assemblySettings

mainClass in assembly := Some("com.ponkotuy.run.Main")

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "com.ponkotuy.build"
