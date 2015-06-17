
name := "MyFleetGirlsServer"

scalaVersion := "2.11.6"

val scalikeJdbcVer = "2.2.+"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % scalikeJdbcVer,
  "org.scalikejdbc" %% "scalikejdbc-config" % scalikeJdbcVer,
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.4.+",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikeJdbcVer,
  "org.json4s" %% "json4s-native" % "3.2.11",
  "com.github.nscala-time" %% "nscala-time" % "2.0.0",
  "mysql" % "mysql-connector-java" % "5.1.+",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test",
  "c3p0" % "c3p0" % "0.9.1.2",
  "org.apache.abdera" % "abdera-parser" % "1.1.3",
  "net.sf.ehcache" % "ehcache" % "2.9.0",
  "org.flywaydb" %% "flyway-play" % "2.0.0-RC2"
)

pipelineStages := Seq(gzip)

includeFilter in (Assets, LessKeys.less) := "*.less"

LessKeys.compress := true

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "build"

scalikejdbcSettings

TwirlKeys.templateImports ++= Seq("views._", "models.db._", "models.join._")
