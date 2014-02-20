
name := "MyFleetGirlsServer"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  "org.json4s" %% "json4s-native" % "3.2.6",
  "org.scalikejdbc" %% "scalikejdbc-async" % "[0.3,)",
  "org.scalikejdbc" %% "scalikejdbc-async-play-plugin" % "[0.3,)",
  "com.github.mauricio" %% "mysql-async" % "0.2.11",
  "ch.qos.logback" % "logback-classic" % "[1.0,)"
)

play.Project.playScalaSettings
