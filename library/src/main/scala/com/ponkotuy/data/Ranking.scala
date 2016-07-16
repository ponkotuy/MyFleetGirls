package com.ponkotuy.data

import com.ponkotuy.tool.RankingDiff
import org.json4s._

case class Ranking(no: Int, nickname: String, rate: Int) extends RankingDiff

object Ranking {
  implicit val format = DefaultFormats
  def fromJson(obj: JValue): List[Ranking] = {
    val JArray(xs) = obj \ "api_list"
    xs.map { x =>
      x.extract[RawRanking].build
    }
  }

  private case class RawRanking(api_no: Int, api_nickname: String, api_rate: Int) {
    def build = Ranking(no = api_no, nickname = api_nickname, rate = api_rate)
  }
}
