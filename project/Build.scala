import scala.sys.{process => p}
import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import sbtbuildinfo.Plugin._

object MyFleetGirlsBuild extends Build {

  val ver = "0.8.2"

  lazy val root = Project(id = "my-fleet-girls", base = file("."), settings = rootSettings)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .aggregate(server, client, library)

  lazy val rootSettings = Defaults.defaultSettings ++ settings ++ Seq(
    commands ++= Seq(proxy, assembl, run, stage, start, zip, genMapper)
  )

  lazy val server = Project(id = "server", base = file("server"))
    .settings(biSettings:_*)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .dependsOn(library)

  lazy val client = Project(id = "client", base = file("client"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .dependsOn(library)

  lazy val library = Project(id = "library", base = file("library"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  override lazy val settings = super.settings ++ Seq(
    version := ver,
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions"),
    jarName in assembly := "MyFleetGirls.jar"
  )

  lazy val biSettings = buildInfoSettings ++ Seq(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.ponkotuy.build"
  )


  def proxy = Command.command("proxy") { state =>
    val subState = Command.process("project client", state)
    Command.process("run", subState)
    state
  }

  def assembl = Command.command("assembly") { state =>
    val subState = Command.process("project client", state)
    Command.process("assembly", subState)
    state
  }

  def run = Command.command("run") { state =>
    val subState = Command.process("project server", state)
    Command.process("run", subState)
    state
  }

  def stage = Command.command("stage") { state =>
    val subState = Command.process("project server", state)
    Command.process("stage", subState)
    state
  }

  def start = Command.command("start") { state =>
    val subState = Command.process("project server", state)
    Command.process("start", subState)
    state
  }

  def genMapper = Command.args("scalikejdbc-gen", "<arg>") { (state, args) =>
    val subState = Command.process("project server", state)
    Command.process("scalikejdbc-gen " + args.mkString(" "), subState)
    state
  }

  def zip = Command.command("zip") { state =>
    Command.process("assembly", state)
    p.Process("""zip -j server/public/zip/MyFleetGirls.zip client/target/scala-2.10/MyFleetGirls.jar application.conf.sample MyFleetGirls.bat MyFleetGirls.sh""").run
    Thread.sleep(10000L)
    state
  }
}
