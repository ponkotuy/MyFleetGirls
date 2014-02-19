package com.ponkotuy.intercept

import java.nio.charset.Charset
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
class PrintContent extends Intercepter {
  def response(res: HttpResponse): Unit = {
    println("Arrived Response")
    println(s"Status: ${res.getStatus}")
    println(s"Headers: ${res.headers().entries()}")
    if(res.headers().get("Content-Type") == "text/plain") {
      println(s"content: ${res.getContent.toString(Charset.forName("UTF-8"))}")
    } else {
      println("Binary(not text).")
    }
  }

  def request(req: HttpRequest): Unit = {
    println("Sending Request")
    println(Map(
      "uri" -> req.getUri,
      "method" -> req.getMethod,
      "header" -> req.headers().entries(),
      "content" -> req.getContent.toString(Charset.forName("UTF-8"))
    ))
  }

  override def input(req: HttpRequest, res: HttpResponse): Unit = {
    request(req)
    response(res)
  }
}
