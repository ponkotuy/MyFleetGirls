package com.ponkotuy.proxy

import java.net.InetSocketAddress
import java.util

import io.netty.handler.codec.http.HttpRequest
import org.apache.http.HttpHost
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.littleshoot.proxy.{ChainedProxy, ChainedProxyAdapter, ChainedProxyManager, HttpFiltersSource, HttpProxyServerBootstrap}

import scala.util.control.NonFatal


class LittleProxy(host: Option[String], port: Int, upstreamProxy: Option[HttpHost], filtersSource: HttpFiltersSource) {

  import LittleProxy._

  private[this] val inetSocketAddress = host.fold(new InetSocketAddress(port)) { address =>
    new InetSocketAddress(address, port)
  }

  private[this] val bootstrap = DefaultHttpProxyServer.bootstrap()
    .withName("MyFleetGirlsProxy")
    .withAddress(inetSocketAddress)
    .withConnectTimeout(30000)
    .withUpstreamProxy(upstreamProxy)
    .withFiltersSource(filtersSource)

  def start(): Unit = {
    try
      bootstrap.start()
    catch {
      case NonFatal(e) =>
        e.printStackTrace()
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
