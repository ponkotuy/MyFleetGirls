import scala.sys.{process => p}
import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import sbtbuildinfo.Plugin._
import play._
import com.typesafe.sbt.web.SbtWeb

object MyFleetGirlsBuild extends Build {

  val ver = "1.3.12"

  val scalaVer = "2.11.6"

  lazy val root = Project(id = "my-fleet-girls", base = file("."), settings = rootSettings)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .aggregate(server, client, library)

  lazy val rootSettings = Defaults.defaultSettings ++ settings ++ Seq(
    commands ++= Seq(proxy, assembl, run, stage, start, dist, zip, genMapper, prof, runTester, runTester2)
  )

  lazy val server = Project(id = "server", base = file("server"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .dependsOn(library)
    .enablePlugins(PlayScala).settings(
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
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions"),
    updateOptions := updateOptions.value.withCircularDependencyLevel(CircularDependencyLevel.Error),
    updateOptions := updateOptions.value.withCachedResolution(true),
    jarName in assembly := "MyFleetGirls.jar",
    incOptions := incOptions.value.withNameHashing(true)
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

  def zip = Command.command("zip") { state =>
    def hash(file: String): Option[String] = {
      (p.Process(s"md5sum ${file}").!!).split(' ').headOption
    }
    Command.process("assembly", state)
    p.Process("""rm server/public/zip/MyFleetGirls.zip""").run()
    p.Process("""rm server/public/client/MyFleetGirls.jar.pack.gz""").run()
    p.Process("""zip -j server/public/zip/MyFleetGirls.zip update/target/update.jar LICENSE update/update.properties update/myfleetgirls.keystore package/resources/application.conf.sample package/resources/MyFleetGirls.bat package/resources/MyFleetGirls.sh package/resources/MyFleetGirls.command package/resources/IE_PROXY.REG""").run()
    if(hash("server/public/client/MyFleetGirls.jar") != hash("client/target/scala-2.11/MyFleetGirls.jar"))
      p.Process("""cp client/target/scala-2.11/MyFleetGirls.jar server/public/client/""").run()
    p.Process("""cp LICENSE MyFleetGirls.bat MyFleetGirls.sh MyFleetGirls.command update/update.properties update/myfleetgirls.keystore package/resources/application.conf.sample package/resources/IE_PROXY.REG server/public/client/""").run()
    p.Process(Seq("sh", "-c", "pack200 --unknown-attribute=pass server/public/client/MyFleetGirls.jar.pack.gz server/public/client/MyFleetGirls.jar 2> /dev/null")).run()
    Thread.sleep(20000L)
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
}
