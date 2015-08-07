package com.ponkotuy.intercept

import akka.actor.{Props, ActorSystem}
import com.netaporter.uri.Uri
import com.ponkotuy.http.ControllerActor
import com.ponkotuy.parser.Query
import com.ponkotuy.util.Log
import com.ponkotuy.value.KCServer
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse}

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

  override def input(req: HttpRequest, res: HttpResponse, uri: Uri): Unit = {
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

  private def valid(req: HttpRequest): Boolean = {
    val uri = Uri.parse(req.getUri)
    uri.host.map(KCServer.ips.contains).getOrElse(true)
  }
}
