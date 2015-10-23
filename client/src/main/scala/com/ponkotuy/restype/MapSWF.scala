package com.ponkotuy.restype

import com.netaporter.uri.Uri
import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.Query

import scala.util.matching.Regex

/**
 *
 * @author ponkotuy
 * Date: 15/07/01.
 */
object MapSWF extends ResType {
  override def regexp: Regex = """\A/kcs/resources/swf/map/\d+_\d+\.swf\z""".r

  override def postables(q: Query): Seq[Result] = {
    val opt = for {
      ver <- q.uri.query.param("version")
      (areaId, infoNo) = parse(q.uri)
      version = ver.filter { c => '0' <= c && c <= '9' }.toInt
      if !MFGHttp.existsMap(areaId, infoNo, version)
    } yield {
      val swf = ShipSWF.allRead(q.response.content)
      FilePostable(s"/swf/map/$areaId/$infoNo/$version", "map", 2, swf, "swf")
    }
    opt.toList
  }

  private def parse(uri: Uri): (Int, Int) = {
    val fName = uri.toString().split('/').toSeq.last
    val (rawArea, rawInfo) = fName.takeWhile(_ != '.').span(_ != '_')
    (rawArea.toInt, rawInfo.tail.toInt)
  }
}
