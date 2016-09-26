package com.ponkotuy.intercept

import akka.actor.{ActorSystem, Props}
import com.netaporter.uri.Uri
import com.ponkotuy.http.ControllerActor
import com.ponkotuy.parser.Query
import com.ponkotuy.util.Log
import io.netty.buffer.ByteBuf

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
class KCInterceptor extends Interceptor with Log {
  val system = ActorSystem()
  val controller = system.actorOf(Props[ControllerActor], "controller")

  override def input(uri: Uri, requestContent: ByteBuf, responseContent: ByteBuf): Unit = {
    requestContent.retain()
    responseContent.retain()
    Future {
      try {
        val q = Query(uri, requestContent, responseContent)
        if (q.parsable) {
          controller ! q
        } else {
          q.release()
        }
      } catch {
        case e: Throwable => logger.error("Query error!",e)
      }
    }
  }

}
