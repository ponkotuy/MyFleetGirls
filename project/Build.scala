import com.typesafe.sbt.web.SbtWeb
import sbt.Keys._
import sbt._
import play._
import sbtassembly.Plugin.AssemblyKeys._

object MyFleetGirlsBuild extends Build {

  val ver = "1.4.2"

  val scalaVer = "2.11.7"

  lazy val root = Project(id = "my-fleet-girls", base = file("."), settings = rootSettings)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .aggregate(server, client, library)

  lazy val rootSettings = settings ++ Seq(
    commands ++= Seq(proxy, assembl, run, stage, start, dist, genMapper, prof, runTester, runTester2, downLib)
  )

  lazy val server = Project(id = "server", base = file("server"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .dependsOn(library)
    .enablePlugins(sbt.PlayScala).settings(
      scalaVersion := scalaVer
    )
    .enablePlugins(SbtWeb)

  lazy val client = Project(id = "client", base = file("client"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .settings(scalaVersion := scalaVer)
    .dependsOn(library)

  lazy val library = Project(id = "library", base = file("library"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .settings(scalaVersion := scalaVer)

  lazy val update = Project(id = "update", base = file("update"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .settings(jarName in assembly := "update.jar")

  lazy val profiler = Project(id = "profiler", base = file("profiler"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .dependsOn(server)

  lazy val tester = Project(id = "tester", base = file("tester"))

  override lazy val settings = super.settings ++ Seq(
    version := ver,
    scalaVersion := scalaVer,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions", "-encoding", "UTF-8"),
    javacOptions ++= Seq("-encoding", "UTF-8"),
    updateOptions := updateOptions.value.withCircularDependencyLevel(CircularDependencyLevel.Error),
    updateOptions := updateOptions.value.withCachedResolution(true),
    jarName in assembly := "MyFleetGirls.jar",
    incOptions := incOptions.value.withNameHashing(true),
    licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html")),
    homepage := Some(url("https://myfleet.moe")),
    fork in Test := true
  )

  def proxy = Command.command("proxy") { state =>
    val subState = Command.process("project client", state)
    Command.process("run", subState)
    state
  }

  def assembl = Command.command("assembly") { state =>
    val updateState = Command.process("project update", state)
    Command.process("assembly", updateState)
    val clientState = Command.process("project client", state)
    Command.process("assembly", clientState)
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

  def dist = Command.command("dist") { state =>
    val subState = Command.process("project server", state)
    Command.process("dist", subState)
    state
  }

  def genMapper = Command.args("scalikejdbc-gen", "<arg>") { (state, args) =>
    val subState = Command.process("project server", state)
    Command.process("scalikejdbc-gen " + args.mkString(" "), subState)
    state
  }

  def prof = Command.command("prof") { state =>
    val subState = Command.process("project  profiler", state)
    Command.process("run", subState)
    state
  }

  def runTester2 = Command.command("runTester") { state =>
    val subState = Command.process("project tester", state)
    Command.process(s"run", subState)
    state
  }

  def runTester = Command.command("runTesterEarth") { state =>
    val subState = Command.process("project tester", state)
    Command.process(s"run https://myfleet.moe", subState)
    state
  }

  def downLib = Command.command("downLib") { state =>
    import scala.sys.process._
    "wget https://www.free-decompiler.com/flash/download/ffdec_5.3.0_lib.jar".!
    "mv ffdec_5.3.0_lib.jar server/lib/".!
    Thread.sleep(1000L)
    state
  }
}
