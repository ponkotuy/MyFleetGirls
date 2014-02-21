import sbt._
import Keys._

object MyFleetGirlsBuild extends Build {
  lazy val root = Project(id = "my-fleet-girls", base = file("."), settings = rootSettings) aggregate(server, client, library)
  lazy val server = Project(id = "server", base = file("server")) dependsOn(library)
  lazy val client = Project(id = "client", base = file("client")) dependsOn(library)
  lazy val library = Project(id = "library", base = file("library"))

  lazy val rootSettings = settings ++ Seq(commands ++= Seq(proxy))

  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")
  )

  def proxy = Command.command("proxy") { state =>
    val subState = Command.process("project client", state)
    Command.process("run", subState)
    state
  }
}
