
name := "MyFleetGirlsServer"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "2.1.+",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.1.+",
  "org.scalikejdbc" %% "scalikejdbc-interpolation" % "2.1.+",
  "org.json4s" %% "json4s-native" % "3.2.11",
  "com.github.tototoshi" %% "play-flyway" % "1.1.2",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0",
  "mysql" % "mysql-connector-java" % "[5.1,)",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "c3p0" % "c3p0" % "0.9.1.2",
  "org.apache.abdera" % "abdera-parser" % "1.1.3"
)

lazy val root = (project in file("server/")).enablePlugins(PlayScala)

includeFilter in (Assets, LessKeys.less) := "*.less"

LessKeys.compress := true

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "build"

scalikejdbcSettings

TwirlKeys.templateImports ++= Seq("views._", "models.db._", "models.join._")
