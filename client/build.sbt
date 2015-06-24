import AssemblyKeys._
import com.typesafe.sbt.license.{LicenseInfo, DepModuleInfo}

// put this at the top of the file

name := "MyFleetGirls"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "com.twitter" %% "finagle-http" % "6.25.0",
  "com.netaporter" %% "scala-uri" % "0.4.7",
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "org.apache.httpcomponents" % "httpmime" % "4.5",
  "com.typesafe.akka" %% "akka-actor" % "2.3.11"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")

assemblySettings

mainClass in assembly := Some("com.ponkotuy.run.Main")

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "com.ponkotuy.build"

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html"))

homepage := Some(url("https://myfleet.moe"))

licenseOverrides := {
  case DepModuleInfo("org.json4s", _, _) | DepModuleInfo("org.apache.httpcomponents", _, _) | DepModuleInfo("com.google.guava", _, _) | DepModuleInfo("commons-codec", _, _) | DepModuleInfo("commons-collections", _, _) | DepModuleInfo("commons-lang", _, _) | DepModuleInfo("commons-logging", _, _) =>
    LicenseInfo(LicenseCategory.Apache, "The Apache Software Licnese, Version 2.0", "http://www.apache.org/licenses/LICENSE-2.0")
}
