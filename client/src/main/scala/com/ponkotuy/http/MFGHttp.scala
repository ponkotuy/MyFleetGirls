package com.ponkotuy.http

import scala.collection.mutable
import scala.collection.JavaConverters._
import java.io._
import java.nio.charset.Charset
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.client.methods.{CloseableHttpResponse, HttpHead, HttpPost}
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import com.ponkotuy.data.{MyFleetAuth, Auth}
import com.ponkotuy.util.Log
import com.ponkotuy.config.ClientConfig
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import com.ponkotuy.parser.SoundUrlId

/** Access To MyFleetGirls
 *
 * @author
 * Date: 14/03/23.
 */
object MFGHttp extends Log {
  val UTF8 = Charset.forName("UTF-8")
  val httpBuilder = HttpClientBuilder.create()

  implicit val formats = Serialization.formats(NoTypeHints)

  def post(uStr: String, data: String, ver: Int = 1)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    if(auth.isEmpty) { info(s"Not Authorized: $uStr"); return }
    try {
      val http = httpBuilder.build()
      val post = new HttpPost(ClientConfig.postUrl(ver) + uStr)
      val entity = createEntity(Map("auth" -> write(auth), "auth2" -> write(auth2), "data" -> data))
      post.setEntity(entity)
      val res = http.execute(post)
      alertResult(res)
    } catch {
      case e: Throwable => error(e.getStackTrace.mkString("\n"))
    }
  }

  def masterPost(uStr: String, data: String, ver: Int = 1)(implicit auth2: Option[MyFleetAuth]): Unit = {
    try {
      val http = httpBuilder.build()
      val post = new HttpPost(ClientConfig.postUrl(ver) + uStr)
      val entity = createEntity(Map("auth2" -> write(auth2), "data" -> data))
      post.setEntity(entity)
      val res = http.execute(post)
      alertResult(res)
    } catch {
      case e: Throwable => error(e.getStackTrace.mkString("\n"))
    }
  }

  private def createEntity(map: Map[String, String]): UrlEncodedFormEntity = {
    val nvps = map.map { case (key, value) =>
      new BasicNameValuePair(key, value)
    }
    new UrlEncodedFormEntity(nvps.asJava, UTF8)
  }

  def postFile(uStr: String, fileBodyKey: String, ver: Int = 1)(file: File)(
      implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    if(auth.isEmpty) { info(s"Not Authorized: $uStr"); return }
    try {
      val http = httpBuilder.build()
      val post = new HttpPost(ClientConfig.postUrl(ver) + uStr)
      val entity = MultipartEntityBuilder.create()
      entity.setCharset(UTF8)
      entity.addTextBody("auth", write(auth), ContentType.APPLICATION_JSON)
      entity.addTextBody("auth2", write(auth2), ContentType.APPLICATION_JSON)
      entity.addPart(fileBodyKey, new FileBody(file))
      post.setEntity(entity.build())
      val res = http.execute(post)
      alertResult(res)
    } catch {
      case e: Throwable => error((e.getMessage :+ e.getStackTrace).mkString("\n"))
    }
  }

  private def alertResult(res: CloseableHttpResponse): Unit = {
    val stCode = res.getStatusLine.getStatusCode
    val content = allRead(res.getEntity.getContent)
    if(stCode >= 400) {
      error(s"Error Response ${stCode}\n${res.getStatusLine}\n${content}")
    }
  }

  def existsImage(key: String): Boolean =
    head(s"/image/ship_obf/$key.jpg", ver = 1).getStatusLine.getStatusCode == 200

  def existsSound(s: SoundUrlId): Boolean =
    head(s"/sound/ship_obf/${s.shipKey}/${s.soundId}.mp3", ver = 1).getStatusLine.getStatusCode == 200

  private def head(uStr: String, ver: Int = 1) = {
    val http = httpBuilder.build()
    val head = new HttpHead(ClientConfig.getUrl(ver) + uStr)
    http.execute(head)
  }

  def allRead(is: InputStream): String = {
    val reader = new InputStreamReader(is)
    val builder = mutable.StringBuilder.newBuilder
    val buf = new Array[Char](1024)
    var num = reader.read(buf)
    while(0 <= num) {
      builder ++= buf
      num = reader.read(buf)
    }
    builder.result()
  }
}
