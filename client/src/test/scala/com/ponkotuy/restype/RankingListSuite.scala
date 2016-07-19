package com.ponkotuy.restype

import java.nio.charset.StandardCharsets

import com.netaporter.uri.Uri
import com.ponkotuy.data.Ranking
import com.ponkotuy.parser.Query
import io.netty.buffer.Unpooled
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write
import org.json4s.{DefaultFormats, JArray, JObject, JValue}
import org.scalatest.FunSuite

import scala.collection.breakOut
import scala.util.Random

class RankingListSuite extends FunSuite {
  implicit def formats = DefaultFormats
  val uri = Uri.parse("http://125.6.189.39/kcsapi/api_req_ranking/getlist")
  test("postables is returned my score") {
    val nickname = "ぽんこつ"
    val admiral = Admiral(None, Some(nickname))
    val rankingList = new RankingList(admiral)
    val c = 3
    val myRanking = dummyRanking(c).copy(nickname = nickname)
    val values: JArray = JArray(((1 to 9).map(_ => dummyRanking(c)) :+ myRanking).map(toJson)(breakOut))
    val json: JObject = ("api_data" -> ("api_list" -> values)) ~ ("api_result" -> 1)
    val query = createQuery(uri, json)
    assert(query.resType.get.isInstanceOf[RankingList])
    val result = write(Ranking(myRanking.no, nickname, myRanking.rate / myRanking.no / c))
    assert(rankingList.postables(query).head.asInstanceOf[NormalPostable].data === result)
  }

  private[this] def dummyRanking(c: Int): Ranking = {
    val no = Random.nextInt(40000)
    Ranking(no, Random.nextInt().toString, Random.nextInt(5000) * c * no)
  }

  private[this] def toJson(ranking: Ranking): JObject =
    ("api_no" -> ranking.no) ~
        ("api_nickname" -> ranking.nickname) ~
        ("api_rate" -> ranking.rate)

  private[this] def createQuery(uri: Uri, json: JValue): Query = {
    val bytes = compact(render(json)).getBytes(StandardCharsets.UTF_8)
    Query(uri, null, Unpooled.wrappedBuffer(bytes))
  }
}
