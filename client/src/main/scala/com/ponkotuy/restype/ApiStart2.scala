package com.ponkotuy.restype

import java.util.Locale

import com.ponkotuy.data.master._
import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.Query
import com.ponkotuy.tool.Checksum
import org.json4s.JsonAST.JValue
import org.json4s.native.Serialization._

import scala.util.matching.Regex

/**
 *
 * @author ponkotuy
 * Date: 15/04/11.
 */
case object ApiStart2 extends ResType {
  import ResType._

  override val regexp: Regex = s"\\A$Api/api_start2\\z".r

  override def postables(q: Query): Seq[HttpPostable] = postablesFromJValue(q.obj)

  private[restype] def postablesFromJValue(obj: JValue, locale: Locale = Locale.getDefault) = {
    if(locale.getLanguage != Locale.JAPANESE.getLanguage || locale.getCountry != Locale.JAPAN.getCountry) {
      Nil
    } else {
      (masterShip(obj) ::
          masterMission(obj) ::
          masterSlotitem(obj) ::
          masterSType(obj) :: Nil).flatten
    }
  }

  private def masterShip(obj: JValue): Option[HttpPostable] = {
    val masterGraph = MasterShipGraph.fromJson(obj \ "api_mst_shipgraph")
    val filenames = masterGraph.map(it => it.id -> it.filename).toMap
    val masterShip = MasterShip.fromJson(obj \ "api_mst_ship", filenames)
    val hash = Checksum.fromSeq(masterShip.map(_.base.name))
    val existingHash = MFGHttp.get("/master/ship/hash", 2).map(_.toLong)
    if(existingHash.exists(_ != hash)) {
      Some(MasterPostable("/master/ship", write(masterShip), 1, "Success sending MasterShip data"))
    } else None
  }

  private def masterMission(obj: JValue): Option[HttpPostable] = {
    val masterMission = MasterMission.fromJson(obj \ "api_mst_mission")
    val hash = Checksum.fromSeq(masterMission.map(_.name))
    val existingHash = MFGHttp.get("/master/mission/hash", 2).map(_.toLong)
    if(existingHash.exists(_ != hash)) {
      Some(MasterPostable("/master/mission", write(masterMission), 1, "Success sending MasterMission data"))
    } else None
  }

  private def masterSlotitem(obj: JValue): Option[HttpPostable] = {
    val masterSlotitem = MasterSlotItem.fromJson(obj \ "api_mst_slotitem")
    val hash = Checksum.fromSeq(masterSlotitem.map(_.name))
    val existingHash = MFGHttp.get("/master/slotitem/hash", 2).map(_.toLong)
    if(existingHash.exists(_ != hash)) {
      Some(MasterPostable("/master/slotitem", write(masterSlotitem), 1, "Success sending MasterSlotitem data"))
    } else None
  }

  private def masterSType(obj: JValue): Option[HttpPostable] = {
    val masterSType = MasterSType.fromJson(obj \ "api_mst_stype")
    val hash = Checksum.fromSeq(masterSType.map(_.name))
    val existingHash = MFGHttp.get("/master/stype/hash", 2).map(_.toLong)
    if(existingHash.exists(_ != hash)) {
      Some(MasterPostable("/master/stype", write(masterSType), 1, "Success sending MasterStype data"))
    } else None
  }
}
