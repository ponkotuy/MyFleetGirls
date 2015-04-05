package com.ponkotuy.data

import org.json4s.native.JsonMethods._
import org.scalatest.FunSuite

/**
 *
 * @author ponkotuy
 * Date: 15/04/06.
 */
class RankingSuite extends FunSuite {
  test("success pattern") {
    val jsonValue = """{"api_result":1,"api_result_msg":"\u6210\u529f","api_data":{"api_count":1000,"api_page_count":100,"api_disp_page":2358,"api_list":[{"api_no":23571,"api_member_id":10007732,"api_level":98,"api_rank":5,"api_nickname":"\u307d\u3093\u3053\u3064","api_experience":961141,"api_comment":"MyFleetGirls","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"110136878","api_comment_id":"135978327"},{"api_no":23572,"api_member_id":10011669,"api_level":87,"api_rank":5,"api_nickname":"phidnight","api_experience":495598,"api_comment":"","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"110147891","api_comment_id":""},{"api_no":23573,"api_member_id":10106588,"api_level":66,"api_rank":5,"api_nickname":"\u307a\u3063\u3061\u3083\u3093","api_experience":229402,"api_comment":"","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"132365803","api_comment_id":""},{"api_no":23574,"api_member_id":10030618,"api_level":107,"api_rank":5,"api_nickname":"\u6771\u90f7\u5e73\u516b\u90ce","api_experience":4429225,"api_comment":"","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"110337740","api_comment_id":""},{"api_no":23575,"api_member_id":10047797,"api_level":101,"api_rank":5,"api_nickname":"agion","api_experience":1839412,"api_comment":"","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"110633623","api_comment_id":""},{"api_no":23576,"api_member_id":10019074,"api_level":92,"api_rank":5,"api_nickname":"\u3060\u3080","api_experience":595912,"api_comment":"\u6469\u8036\u69d8\u3068\u767d\u9732\u578b\u304b\u308f\u3044\u3044","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"110211449","api_comment_id":"135154766"},{"api_no":23577,"api_member_id":10092483,"api_level":88,"api_rank":5,"api_nickname":"\u30ab\u30f3\u30c7\u30aa\u30f3","api_experience":517317,"api_comment":"\u653e\u7f6e\u5c11\u96bb\u3002\u304b\u3082\u3002","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"129283726","api_comment_id":"135363701"},{"api_no":23578,"api_member_id":10000648,"api_level":83,"api_rank":5,"api_nickname":"\u30b8\u30f3\u30b8\u30e3\u30fc\u30a8\u30fc\u30eb","api_experience":437004,"api_comment":"\u305f\u3060\u3044\u307e","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"110124991","api_comment_id":"135830092"},{"api_no":23579,"api_member_id":10037563,"api_level":100,"api_rank":5,"api_nickname":"hatomas28","api_experience":1339645,"api_comment":"\u3042\u3051\u307e\u3057\u3066\u304a\u3081\u3067\u3068\u3046\u3054\u3056","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"110508777","api_comment_id":"131064997"},{"api_no":23580,"api_member_id":10041144,"api_level":99,"api_rank":5,"api_nickname":"Baka","api_experience":1010696,"api_comment":"\u975e\u7406\u6cd5\u6a29\u5929","api_rate":12,"api_flag":0,"api_medals":0,"api_nickname_id":"110515711","api_comment_id":"123439816"}]}}"""
    val expected = Ranking(23571, 10007732L, 12)
    val value = Ranking.fromJson(parse(jsonValue) \ "api_data").filter(_.memberId == expected.memberId)
    assert(value.nonEmpty)
    assert(value.head === expected)
  }
}
