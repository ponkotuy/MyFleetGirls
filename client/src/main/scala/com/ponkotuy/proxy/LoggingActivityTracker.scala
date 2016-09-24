package com.ponkotuy.proxy

import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse

import java.net.InetSocketAddress
import javax.net.ssl.SSLSession

import org.littleshoot.proxy._

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggingActivityTracker extends ActivityTrackerAdapter {

  lazy val logger = LoggerFactory.getLogger(getClass)

  // override def bytesReceivedFromClient(flowContext: FlowContext,numberOfBytes: Int): Unit = {}

  override def requestReceivedFromClient(flowContext: FlowContext,httpRequest: HttpRequest): Unit = {}

  // override def bytesSentToServer(flowContext: FullFlowContext,numberOfBytes: Int): Unit = {}

  override def requestSentToServer(flowConext: FullFlowContext,httpRequest: HttpRequest): Unit = {}

  override def bytesReceivedFromServer(flowConext: FullFlowContext,numberOfBytes: Int): Unit = {}

  override def responseReceivedFromServer(flowContext: FullFlowContext,httpResponce: HttpResponse): Unit = {}

  // override def bytesSentToClient(flowContext: FullFlowContext,numberOfBytes: Int): Unit = {}

  override def responseSentToClient(flowContext: FlowContext,httpResponse: HttpResponse): Unit = {}

  override def clientConnected(clientAddress: InetSocketAddress): Unit = {}

  // override def clientSSLHandshakeSucceeded(clientAddress: InetSocketAddress,sslSession: SSLSession): Unit = {}

  override def clientDisconnected(clientAddress: InetSocketAddress,sslSession: SSLSession): Unit = {}
}

