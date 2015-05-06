package com.ponkotuy.http

import java.io.IOException

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.apache.http.protocol.HttpContext

import scala.util.Random

/**
 * Retry時にwaitBase * 1.0 〜 1.5程度のWaitをかける
 * @author ponkotuy
 * Date: 15/04/13.
 */
class RetryWithWait(val count: Int, val waitBase: Long) extends DefaultHttpRequestRetryHandler(count, true) {
  val random = new Random()
  override def retryRequest(exception: IOException, execCount: Int, context: HttpContext): Boolean = {
    val retry = super.retryRequest(exception, execCount, context)
    println("Connection failed.")
    if(retry) {
      println("Retry...")
      val rate = random.nextDouble() * 0.5 + 1.0
      Thread.sleep((waitBase * rate).toLong)
    }
    retry
  }
}
