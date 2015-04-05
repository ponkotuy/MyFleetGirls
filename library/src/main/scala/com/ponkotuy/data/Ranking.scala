package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 15/04/06.
 */
case class Ranking(no: Int, memberId: Long, rate: Int)

object Ranking {
  implicit val format = DefaultFormats
  def fromJson(obj: JValue): List[Ranking] = {
    val JArray(xs) = obj \ "api_list"
    xs.map { x =>
      x.extract[RawRanking].build
    }
  }

  private case class RawRanking(api_no: Int, api_member_id: Long, api_rate: Int) {
    def build = Ranking(no = api_no, memberId = api_member_id, rate = api_rate)
  }
}
