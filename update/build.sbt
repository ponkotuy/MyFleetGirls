import AssemblyKeys._

// put this at the top of the file

name := "MyFleetGirlsUpdate"

crossPaths := false

autoScalaLibrary := false

libraryDependencies ++= Seq()

assemblySettings

mainClass in assembly := Some("Main")

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html"))

homepage := Some(url("https://myfleet.moe"))
