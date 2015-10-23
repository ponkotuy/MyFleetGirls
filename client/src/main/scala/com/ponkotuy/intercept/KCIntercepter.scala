package com.ponkotuy.intercept

import akka.actor.{ActorSystem, Props}
import com.netaporter.uri.Uri
import com.ponkotuy.http.ControllerActor
import com.ponkotuy.parser.Query
import com.ponkotuy.util.Log
import com.ponkotuy.value.KCServer
import com.twitter.finagle.http.{Request, Response}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
class KCIntercepter extends Intercepter with Log {
  val system = ActorSystem()
  val controller = system.actorOf(Props[ControllerActor], "controller")

  override def input(req: Request, res: Response, uri: Uri): Unit = {
    Future {
      if(!valid(req)) return
      try {
        val q = Query(req, res, uri)
        if(q.parsable) controller ! q
      } catch {
        case e: Throwable => error((e.getMessage +: e.getStackTrace).mkString("\n"))
      }
    }
  }

  private def valid(req: Request): Boolean = {
    val uri = Uri.parse(req.uri)
    uri.host.map(KCServer.ips.contains).getOrElse(true)
  }
}
