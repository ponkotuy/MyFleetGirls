
name := "MyFleetGirlsLibrary"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test"
)

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html"))

homepage := Some(url("https://myfleet.moe"))
