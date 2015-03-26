package com.ponkotuy.intercept

import com.netaporter.uri.Uri
import com.ponkotuy.parser.{Query, ResponseController}
import com.ponkotuy.util.Log
import com.ponkotuy.value.KCServer
import io.netty.handler.codec.http.HttpRequest

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
class KCIntercepter extends Intercepter with Log {
  val controller = new ResponseController
  override def input(req: HttpRequest, res: Array[Byte], uri: Uri): Unit = {
    Future {
      if(!valid(req)) return
      try {
        val q = Query(req, res, uri)
        try {
          if(q.parsable) controller.query(q)
        } finally {
          q.close()
        }
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
