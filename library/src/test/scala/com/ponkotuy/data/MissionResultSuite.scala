package com.ponkotuy.data

import org.scalatest.FunSuite
import org.json4s.native.JsonMethods._

/**
 * @author ponkotuy
 * Date: 15/03/07.
 */
class MissionResultSuite extends FunSuite {
  test("a success pattern") {
    val rawValue = """{"api_result":1,"api_result_msg":"\u6210\u529f","api_data":{"api_ship_id":[-1,89,1844,203,234,14,217],"api_clear_result":1,"api_get_exp":70,"api_member_lv":98,"api_member_exp":936313,"api_get_ship_exp":[90,60,60,60,60,60],"api_get_exp_lvup":[[56932,59500],[54420,56100],[32733,35100],[52976,56100],[36060,37800],[56819,59500]],"api_maparea_name":"\u5357\u897f\u8af8\u5cf6\u6d77\u57df","api_detail":"\u5feb\u901f\u306e\u99c6\u9010\u8266\u3092\u96c6\u4e2d\u904b\u7528\u3057\u3066\u3001\u6fc0\u6226\u306e\u8af8\u5cf6\u3078\u7269\u8cc7\u3092\u8f38\u9001\u3057\u3088\u3046\uff01","api_quest_name":"\u9f20\u8f38\u9001\u4f5c\u6226","api_quest_level":5,"api_get_material":[240,300,0,0],"api_useitem_flag":[1,0],"api_get_item1":{"api_useitem_id":-1,"api_useitem_name":null,"api_useitem_count":1}}}"""
    val jsonValue = parse(rawValue)
    val value = MissionResult.fromJson(jsonValue \ "api_data")
    assert(value.isDefined)
    assert(value.get.shipIds.size === 6)
    assert(value.get.getItem === None)
    assert(value.get.result === 1)
  }

  test("a big success with useitem pattern") {
    val rawValue = """{"api_result":1,"api_result_msg":"\u6210\u529f","api_data":{"api_ship_id":[-1,2223,95,166,2392,7,2439],"api_clear_result":2,"api_get_exp":100,"api_member_lv":98,"api_member_exp":936413,"api_get_ship_exp":[420,280,280,280,280,280],"api_get_exp_lvup":[[221432,228000],[98823,99000,103500],[91896,94600],[71064,74100],[85025,86100],[78171,82000]],"api_maparea_name":"\u5357\u65b9\u6d77\u57df","api_detail":"\u6c34\u96f7\u6226\u968a\u306b\u30c9\u30e9\u30e0\u7f36(\u8f38\u9001\u7528)\u3092\u53ef\u80fd\u306a\u9650\u308a\u6e80\u8f09\u3057\u3001\u5357\u65b9\u9f20\u8f38\u9001\u4f5c\u6226\u3092\u7d9a\u884c\u305b\u3088\uff01","api_quest_name":"\u6771\u4eac\u6025\u884c(\u5f10)","api_quest_level":8,"api_get_material":[630,0,300,0],"api_useitem_flag":[4,0],"api_get_item1":{"api_useitem_id":10,"api_useitem_name":"\u5bb6\u5177\u7bb1\uff08\u5c0f\uff09","api_useitem_count":1}}}"""
    val jsonValue = parse(rawValue)
    val value = MissionResult.fromJson(jsonValue \ "api_data")
    assert(value.isDefined)
    assert(value.get.shipIds.size === 6)
    assert(value.get.getItem.isDefined)
    assert(value.get.result === 2)
  }
}
