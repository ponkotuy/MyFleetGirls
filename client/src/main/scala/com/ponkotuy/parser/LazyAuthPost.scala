package com.ponkotuy.parser

import scala.collection.mutable
import com.ponkotuy.data.{MyFleetAuth, Auth}
import com.ponkotuy.config.ClientConfig

/**
 *
 * @author ponkotuy
 * Date: 14/06/01.
 */
class LazyAuthPost {
  val seq: mutable.Buffer[(Option[Auth], Option[MyFleetAuth]) => Unit] = mutable.Buffer()
  val MaxSize = 20
  var auth: Option[Auth] = None

  def apply(f: (Option[Auth], Option[MyFleetAuth]) => Unit) = post(f)

  def setAuth(auth: Auth): Unit = {
    synchronized {
      this.auth = Some(auth)
      seq.map(f => f(this.auth, auth2))
      seq.clear()
    }
  }

  def post(postFunc: (Option[Auth], Option[MyFleetAuth]) => Unit): Unit = {
    synchronized {
      if(auth.isDefined) {
        postFunc(auth, auth2)
      }
      else add(postFunc)
    }
  }

  private def add(f: (Option[Auth], Option[MyFleetAuth]) => Unit): Unit = {
    seq += f
    if(seq.size > MaxSize) seq.remove(0)
  }

  private def auth2: Option[MyFleetAuth] = auth.flatMap(a => ClientConfig.auth(a.memberId))
}
