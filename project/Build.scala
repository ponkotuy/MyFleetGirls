import sbt._
import Keys._

object MyFleetGirlsBuild extends Build {
  lazy val root = Project(id = "my-fleet-girls", base = file("."), settings = rootSettings)
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .aggregate(server, client, library)

  lazy val rootSettings = Defaults.defaultSettings ++ settings ++ Seq(
    commands ++= Seq(proxy, run)
  )

  lazy val server = Project(id = "server", base = file("server"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .dependsOn(library)

  lazy val client = Project(id = "client", base = file("client"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
    .dependsOn(library)

  lazy val library = Project(id = "library", base = file("library"))
    .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")
  )

  def proxy = Command.command("proxy") { state =>
    val subState = Command.process("project client", state)
    Command.process("run", subState)
    state
  }

  def run = Command.command("run") { state =>
    val subState = Command.process("project server", state)
    Command.process("run", subState)
    state
  }
}
