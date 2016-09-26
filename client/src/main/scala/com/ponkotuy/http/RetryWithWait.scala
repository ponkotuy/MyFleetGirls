package com.ponkotuy.http

import java.io.IOException

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.apache.http.protocol.HttpContext

import scala.util.Random

import com.ponkotuy.util.Log

/**
 * Retry時にwaitBase * 1.0 〜 1.5程度のWaitをかける
 * @author ponkotuy
 * Date: 15/04/13.
 */
class RetryWithWait(val count: Int, val waitBase: Long) extends DefaultHttpRequestRetryHandler(count, true) with Log {
  val random = new Random()
  override def retryRequest(exception: IOException, execCount: Int, context: HttpContext): Boolean = {
    val retry = super.retryRequest(exception, execCount, context)
    logger.debug("Connection failed.")
    if(retry) {
      val rate = random.nextDouble() * 0.5 + 1.0
      val waitMillis = (waitBase * rate).toLong
      logger.debug("Retry... waiting {} milliseconds.",waitMillis)
      Thread.sleep(waitMillis)
    }
    retry
  }
}
