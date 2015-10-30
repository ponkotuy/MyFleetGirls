import com.typesafe.sbt.web.SbtWeb
import sbt.Keys._
import sbt._
import play._
import sbtassembly.AssemblyPlugin.autoImport._
import sbtbuildinfo.BuildInfoPlugin

object MyFleetGirlsBuild extends Build {

  val ver = "1.4.10"

  val scalaVer = "2.11.7"

  lazy val root = Project(id = "my-fleet-girls", base = file("."), settings = rootSettings)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings)
    .aggregate(server, client, library)

  lazy val rootSettings = settings ++ Seq(
    commands ++= Seq(proxy, assembl, run, stage, start, dist, genMapper, prof, runTester, runTester2, downLib)
  )

  lazy val server = project
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings)
    .dependsOn(library)
    .enablePlugins(sbt.PlayScala).settings(
      scalaVersion := scalaVer
    )
    .enablePlugins(SbtWeb, BuildInfoPlugin)

  lazy val client = project
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings)
    .settings(scalaVersion := scalaVer)
    .dependsOn(library)
    .enablePlugins(BuildInfoPlugin)

  lazy val library = project
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings)
    .settings(scalaVersion := scalaVer)

  lazy val update = project
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings)
    .settings(assemblyJarName in assembly := "update.jar")

  lazy val profiler = project
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings)
    .dependsOn(server)

  lazy val tester = project

  override lazy val settings = super.settings ++ Seq(
    version := ver,
    scalaVersion := scalaVer,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions", "-encoding", "UTF-8"),
    javacOptions ++= Seq("-encoding", "UTF-8"),
    updateOptions := updateOptions.value.withCircularDependencyLevel(CircularDependencyLevel.Error),
    updateOptions := updateOptions.value.withCachedResolution(true),
    assemblyJarName in assembly := "MyFleetGirls.jar",
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
    IO.withTemporaryFile("ffdec-", ".tmp") { tmp =>
      val dlUrl = url("https://www.free-decompiler.com/flash/download/ffdec_5.3.0_lib.jar")
      state.log.info(s"downloading $dlUrl ...")
      IO.download(dlUrl, tmp)
      IO.move(tmp, file("server") / "lib" / "ffdec_5.3.0_lib.jar")
    }
    state
  }
}
