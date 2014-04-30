
name := "MyFleetGirlsServer"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "1.7.5",
  "org.scalikejdbc" %% "scalikejdbc-config" % "1.7.5",
  "org.scalikejdbc" %% "scalikejdbc-interpolation" % "1.7.5",
//  "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "1.7.5",
  "org.json4s" %% "json4s-native" % "3.2.8",
  "com.github.tototoshi" %% "play-flyway" % "1.0.3",
  "mysql" % "mysql-connector-java" % "[5.1,)",
  "org.scalatest" %% "scalatest" % "2.1.2" % "test",
  "c3p0" % "c3p0" % "0.9.1.2"
)

play.Project.playScalaSettings

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "build"

scalikejdbcSettings
