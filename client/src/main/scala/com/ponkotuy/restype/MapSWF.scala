package com.ponkotuy.restype

import com.netaporter.uri.Uri
import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.Query

import scala.util.Try
import scala.util.matching.Regex

/**
 * Normal swf stage
 */
object MapSWF extends ResType {
  override val regexp: Regex = """\A/kcs/resources/swf/map/\d+_\d+\.swf\z""".r

  override def postables(q: Query): Seq[Result] = {
    val (areaId, infoNo) = parse(q.uri)
    val version = CommonMapSWF.parseVersion(q).getOrElse(0)
    if(MFGHttp.existsMap(areaId, infoNo, version)) Nil
    else {
      val swf = CommonMapSWF.readSWF(q)
      Seq(FilePostable(s"/swf/map/$areaId/$infoNo/$version", "map", 2, swf, "swf"))
    }
  }

  private def parse(uri: Uri): (Int, Int) = {
    val fName = uri.toString().split('/').toSeq.last
    val (rawArea, rawInfo) = fName.takeWhile(_ != '.').span(_ != '_')
    (rawArea.toInt, rawInfo.tail.toInt)
  }
}

/** Obfuscated swf stage */
object ObfuscatedMapSWF extends ResType {
  override val regexp: Regex = """\A/kcs/resources/swf/map/(.+).swf\z""".r

  override def postables(q: Query): Seq[Result] = {
    val opt = for {
      areaId <- MapStart.areaId
      infoNo <- MapStart.infoNo
      version = CommonMapSWF.parseVersion(q).getOrElse(0)
      if !MFGHttp.existsMap(areaId, infoNo, version)
    } yield {
      val swf = CommonMapSWF.readSWF(q)
      FilePostable(s"/swf/map/$areaId/$infoNo/$version", "map", 2, swf, "swf")
    }
    opt.toList
  }
}

object CommonMapSWF {
  def readSWF(q: Query): Array[Byte] = ShipSWF.allRead(q.response.content)
  def parseVersion(q: Query): Option[Int] = q.uri.query.param("version").flatMap(extractNumber)
  def extractNumber(str: String): Option[Int] = Try {
    str.filter { c => '0' <= c && c <= '9' }.toInt
  }.toOption
}
