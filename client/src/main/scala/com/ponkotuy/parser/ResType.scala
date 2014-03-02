package com.ponkotuy.parser

import com.github.theon.uri.Uri
import com.ponkotuy.util.Log

/**
 *
 * @author ponkotuy
 * Date: 14/02/19.
 */
sealed abstract class ResType(val path: String)

object ResType extends Log {
  val Api = "/kcsapi"
  val GetMaster = s"$Api/api_get_master"
  val GetMember = s"$Api/api_get_member"
  val ReqKousyou = s"$Api/api_req_kousyou"

  case object Material extends ResType(s"$GetMember/material")
  case object Basic extends ResType(s"$GetMember/basic")
  case object Ship3 extends ResType(s"$GetMember/ship3")
  case object NDock extends ResType(s"$GetMember/ndock")
  case object KDock extends ResType(s"$GetMember/kdock")
  case object DeckPort extends ResType(s"$GetMember/deck_port")
  case object Record extends ResType(s"$GetMember/record") // Basicの綺麗版
  case object CreateShip extends ResType(s"$ReqKousyou/createship")
  case object GetShip extends ResType(s"$ReqKousyou/getship") // IDとshipIDのみ
  case object CreateItem extends ResType(s"$ReqKousyou/createitem")
  case object MasterShip extends ResType(s"$GetMaster/ship")

  val values = Set(Material, Basic, Ship3, NDock, KDock, DeckPort, Record, CreateShip, GetShip, CreateItem, MasterShip)

  def fromUri(uri: String): Option[ResType] = {
    val path = Uri.parseUri(uri).pathRaw
    println(path)
    values.find(_.path == path)
  }
}
