
name := "MyFleetGirlsServer"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "2.0.0",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.0.0",
  "org.scalikejdbc" %% "scalikejdbc-interpolation" % "2.0.0",
  "org.json4s" %% "json4s-native" % "3.2.9",
  "com.github.tototoshi" %% "play-flyway" % "1.1.0",
  "com.github.nscala-time" %% "nscala-time" % "1.0.0",
  "com.newrelic.agent.java" % "newrelic-agent" % "3.7.2",
  "mysql" % "mysql-connector-java" % "[5.1,)",
  "org.scalatest" %% "scalatest" % "2.1.5" % "test",
  "c3p0" % "c3p0" % "0.9.1.2"
)

lazy val root = (project in file("server/")).enablePlugins(PlayScala)

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "build"

scalikejdbcSettings
