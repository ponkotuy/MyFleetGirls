
name := "MyFleetGirlsServer"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "1.7.4",
  "org.scalikejdbc" %% "scalikejdbc-config" % "1.7.4",
  "org.scalikejdbc" %% "scalikejdbc-interpolation" % "1.7.4",
  "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "1.7.4",
  "org.json4s" %% "json4s-native" % "3.2.6",
  "org.scalikejdbc" %% "scalikejdbc-async" % "[0.3,)",
  "org.scalikejdbc" %% "scalikejdbc-async-play-plugin" % "[0.3,)",
  "com.github.mauricio" %% "mysql-async" % "0.2.11",
  "com.github.tototoshi" %% "play-flyway" % "1.0.3",
  "mysql" % "mysql-connector-java" % "[5.1,)"
)

play.Project.playScalaSettings
