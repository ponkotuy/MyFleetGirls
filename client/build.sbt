import com.typesafe.sbt.license.{LicenseInfo, DepModuleInfo}

// put this at the top of the file

name := "MyFleetGirls"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "com.twitter" %% "finagle-http" % "6.30.0",
  "com.netaporter" %% "scala-uri" % "0.4.10",
  "org.apache.httpcomponents" % "httpclient" % "4.5.1",
  "org.apache.httpcomponents" % "httpmime" % "4.5.1",
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")

mainClass in assembly := Some("com.ponkotuy.run.Main")

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "com.ponkotuy.build"

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html"))

homepage := Some(url("https://myfleet.moe"))

licenseOverrides := {
  case DepModuleInfo("org.json4s", _, _) | DepModuleInfo("org.apache.httpcomponents", _, _) | DepModuleInfo("com.google.guava", _, _) | DepModuleInfo("commons-codec", _, _) | DepModuleInfo("commons-collections", _, _) | DepModuleInfo("commons-lang", _, _) | DepModuleInfo("commons-logging", _, _) =>
    LicenseInfo(LicenseCategory.Apache, "The Apache Software Licnese, Version 2.0", "http://www.apache.org/licenses/LICENSE-2.0")
}
