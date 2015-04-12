package com.ponkotuy.http

import com.netaporter.uri.Uri
import com.ponkotuy.value.KCServer

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
object Host {
  private[this] var kcServer: Option[KCServer] = None
  def get(): Option[KCServer] = kcServer
  def set(uri: Uri): Unit = {
    if(kcServer.isEmpty) {
      val opt = for {
        host <- uri.host
        server <- KCServer.fromIP(host)
      } yield server
      kcServer = opt
    }
  }
}
