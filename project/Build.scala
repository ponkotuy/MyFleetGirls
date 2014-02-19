import sbt._
import Keys._

object MyFleetGirlsBuild extends Build {
  lazy val root = Project(id = "my-fleet-girls", base = file(".")) aggregate(server, client, library)
  lazy val server = Project(id = "server", base = file("server"))
  lazy val client = Project(id = "client", base = file("client"))
  lazy val library = Project(id = "library", base = file("library"))
}
