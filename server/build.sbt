
name := "MyFleetGirlsServer"

val scalikeJdbcVer = "2.4.1"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % scalikeJdbcVer,
  "org.scalikejdbc" %% "scalikejdbc-config" % scalikeJdbcVer,
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.1",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikeJdbcVer,
  "com.github.nscala-time" %% "nscala-time" % "2.12.0",
  "mysql" % "mysql-connector-java" % "5.1.39",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.apache.abdera" % "abdera-parser" % "1.1.3",
  "net.sf.ehcache" % "ehcache" % "2.10.2",
  "org.flywaydb" %% "flyway-play" % "3.0.0"
)

routesGenerator := InjectedRoutesGenerator

pipelineStages := Seq(gzip)

includeFilter in (Assets, LessKeys.less) := "*.less"

LessKeys.compress := true

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "build"

scalikejdbcSettings

TwirlKeys.templateImports ++= Seq("views._", "models.db._", "models.join._")

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html"))

homepage := Some(url("https://myfleet.moe"))

// docker
dockerRepository := Some("ponkotuy")
dockerUpdateLatest := true
