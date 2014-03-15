
name := "MyFleetGirlsServer"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "1.7.4",
  "org.scalikejdbc" %% "scalikejdbc-config" % "1.7.4",
  "org.scalikejdbc" %% "scalikejdbc-interpolation" % "1.7.4",
  "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "1.7.4",
  "org.json4s" %% "json4s-native" % "3.2.6",
  "com.github.tototoshi" %% "play-flyway" % "1.0.3",
  "mysql" % "mysql-connector-java" % "[5.1,)"
)

play.Project.playScalaSettings

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "build"
