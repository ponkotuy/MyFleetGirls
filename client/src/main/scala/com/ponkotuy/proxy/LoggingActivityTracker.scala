package com.ponkotuy.proxy

import java.net.InetSocketAddress
import javax.net.ssl.SSLSession

import io.netty.handler.codec.http.{HttpRequest,HttpResponse}
import io.netty.handler.codec.http.HttpHeaders

import org.littleshoot.proxy._

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggingActivityTracker extends ActivityTrackerAdapter {

  lazy val logger = LoggerFactory.getLogger(getClass)

  override def requestReceivedFromClient(flowContext: FlowContext,httpRequest: HttpRequest): Unit = {
    logger.debug("request received from client to proxy. URL:{}",httpRequest.getUri)
  }

  override def requestSentToServer(flowConext: FullFlowContext,httpRequest: HttpRequest): Unit = {
    logger.debug("request sent proxy to server. URL:{}",httpRequest.getUri)
  }

  override def bytesReceivedFromServer(flowConext: FullFlowContext,numberOfBytes: Int): Unit = {
    logger.trace("response received from server to proxy. {} bytes",numberOfBytes)
  }

  override def responseReceivedFromServer(flowContext: FullFlowContext,httpResponse: HttpResponse): Unit = {
    logger.debug(
      "response received from server to proxy. Status:{}, Transfer:{}, Content:{}",
      httpResponse.getStatus,
      httpResponse.headers.get(HttpHeaders.Names.TRANSFER_ENCODING),
      httpResponse.headers.get(HttpHeaders.Names.CONTENT_ENCODING)
    )
  }

  override def responseSentToClient(flowContext: FlowContext,httpResponse: HttpResponse): Unit = {
    logger.debug(
      "response sent to client from proxy. Status:{}, Transfer:{}, Content:{}",
      httpResponse.getStatus,
      httpResponse.headers.get(HttpHeaders.Names.TRANSFER_ENCODING),
      httpResponse.headers.get(HttpHeaders.Names.CONTENT_ENCODING)
    )
  }

  override def clientConnected(clientAddress: InetSocketAddress): Unit = {
    logger.info("Client Connected from:{}",clientAddress.toString);
  }

  override def clientDisconnected(clientAddress: InetSocketAddress,sslSession: SSLSession): Unit = {
    logger.info("Client DisConnected from:{}",clientAddress.toString)
  }
}

