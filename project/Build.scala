import sbt._
import Keys._

object MyFleetGirlsBuild extends Build {
  lazy val root = Project(id = "my-fleet-girls", base = file(".")) aggregate(server, client, library)
  lazy val server = Project(id = "server", base = file("server")) dependsOn(library)
  lazy val client = Project(id = "client", base = file("client")) dependsOn(library)
  lazy val library = Project(id = "library", base = file("library"))
}
