package com.ponkotuy.proxy

import java.net.InetSocketAddress
import java.util

import io.netty.handler.codec.http.HttpRequest
import org.apache.http.HttpHost
import org.littleshoot.proxy.impl.{DefaultHttpProxyServer, ThreadPoolConfiguration}
import org.littleshoot.proxy.{ChainedProxy, ChainedProxyAdapter, ChainedProxyManager, HttpFiltersSource, HttpProxyServerBootstrap}

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

class LittleProxy(host: Option[String], port: Int, upstreamProxy: Option[HttpHost], filtersSource: HttpFiltersSource) {

  import LittleProxy._

  lazy val logger = LoggerFactory.getLogger(getClass)

  private[this] val inetSocketAddress = host.fold(new InetSocketAddress(port)) { address =>
    new InetSocketAddress(address, port)
  }

  // 猫対策お試しでThreadを減らしている
  val defaultThreadPoolConf = new ThreadPoolConfiguration()
    .withAcceptorThreads(1)
    .withClientToProxyWorkerThreads(2)
    .withProxyToServerWorkerThreads(2)

  private[this] val bootstrap = DefaultHttpProxyServer.bootstrap()
    .withName("MyFleetGirlsProxy")
    .withAddress(inetSocketAddress)
    .withConnectTimeout(30000)
    .withUpstreamProxy(upstreamProxy)
    .withFiltersSource(filtersSource)
    .withThreadPoolConfiguration(defaultThreadPoolConf)

  def start(): Unit = {
    try
      bootstrap.start()
    catch {
      case NonFatal(e) =>
        logger.error("Can't start proxy.")
        println("多重起動していないかどうか確認してください")
        sys.exit(1)
    }
  }

}

object LittleProxy {

  implicit class HttpProxyServerBootstrapExtension(private val self: HttpProxyServerBootstrap) extends AnyVal {

    def withUpstreamProxy(upstreamProxy: Option[HttpHost]): HttpProxyServerBootstrap =
      upstreamProxy.fold(self) { upstream =>
        val addr = new InetSocketAddress(upstream.getHostName, upstream.getPort)
        val proxy = new ChainedProxyAdapter {
          override def getChainedProxyAddress: InetSocketAddress = addr
        }
        self.withChainProxyManager(new ChainedProxyManager {
          def lookupChainedProxies(httpRequest: HttpRequest, chainedProxies: util.Queue[ChainedProxy]): Unit =
            chainedProxies.add(proxy)
        })
      }

  }

}
