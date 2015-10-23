
name := "MyFleetGirlsServer"

scalaVersion := "2.11.7"

val scalikeJdbcVer = "2.2.9"

resolvers +=
"Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % scalikeJdbcVer,
  "org.scalikejdbc" %% "scalikejdbc-config" % scalikeJdbcVer,
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.4.2",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikeJdbcVer,
  "com.github.nscala-time" %% "nscala-time" % "2.2.0",
  "mysql" % "mysql-connector-java" % "5.1.37",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.apache.abdera" % "abdera-parser" % "1.1.3",
  "net.sf.ehcache" % "ehcache" % "2.10.1",
  "org.flywaydb" %% "flyway-play" % "2.2.0"
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

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html"))

homepage := Some(url("https://myfleet.moe"))
