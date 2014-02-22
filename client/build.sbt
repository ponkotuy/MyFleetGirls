
name := "MyFleetGirls"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.0",
  "com.twitter" %% "finagle-http" % "6.10.0",
  "com.github.theon" %% "scala-uri" % "0.3.6",
  "org.json4s" %% "json4s-native" % "3.2.6",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")
