package com.ponkotuy.intercept

import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import com.ponkotuy.parser.{Query, ResponseController}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import com.ponkotuy.util.Log
import com.ponkotuy.value.KCServer
import com.github.theon.uri.Uri

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
class KCIntercepter extends Intercepter with Log {
  val controller = new ResponseController
  override def input(req: HttpRequest, res: HttpResponse): Unit = {
    if(!valid(req)) return
    Future {
      try {
        val q = Query(req, res)
        if(q.parsable) controller.query(q)
      } catch {
        case e: Throwable => error((e.getMessage +: e.getStackTrace).mkString("\n"))
      }
    }
  }

  private def valid(req: HttpRequest): Boolean = {
    val uri = Uri.parseUri(req.getUri)
    uri.host.map(KCServer.ips.contains).getOrElse(false)
  }
}
