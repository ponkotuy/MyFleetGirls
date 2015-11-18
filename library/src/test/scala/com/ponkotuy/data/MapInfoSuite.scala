package com.ponkotuy.data

import org.scalatest.FunSuite
import org.json4s.native.JsonMethods._

/**
 *
 * @author ponkotuy
 * Date:  15/08/26.
 */
class MapInfoSuite extends FunSuite {
  test("success pattern before start event") {
    val jsonValue = """{"api_result":1,"api_result_msg":"\u6210\u529f","api_data":[{"api_id":11,"api_cleared":1,"api_exboss_flag":0},{"api_id":12,"api_cleared":1,"api_exboss_flag":0},{"api_id":13,"api_cleared":1,"api_exboss_flag":0},{"api_id":14,"api_cleared":1,"api_exboss_flag":0},{"api_id":15,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":21,"api_cleared":1,"api_exboss_flag":0},{"api_id":22,"api_cleared":1,"api_exboss_flag":0},{"api_id":23,"api_cleared":1,"api_exboss_flag":0},{"api_id":24,"api_cleared":1,"api_exboss_flag":0},{"api_id":25,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":31,"api_cleared":1,"api_exboss_flag":0},{"api_id":32,"api_cleared":1,"api_exboss_flag":0},{"api_id":33,"api_cleared":1,"api_exboss_flag":0},{"api_id":34,"api_cleared":1,"api_exboss_flag":0},{"api_id":35,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":41,"api_cleared":1,"api_exboss_flag":0},{"api_id":42,"api_cleared":1,"api_exboss_flag":0},{"api_id":43,"api_cleared":1,"api_exboss_flag":0},{"api_id":44,"api_cleared":1,"api_exboss_flag":0},{"api_id":45,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":51,"api_cleared":1,"api_exboss_flag":0},{"api_id":52,"api_cleared":1,"api_exboss_flag":0},{"api_id":53,"api_cleared":1,"api_exboss_flag":0},{"api_id":54,"api_cleared":1,"api_exboss_flag":0},{"api_id":55,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":61,"api_cleared":0,"api_exboss_flag":0},{"api_id":311,"api_cleared":0,"api_eventmap":{"api_now_maphp":9999,"api_max_maphp":9999,"api_state":1,"api_selected_rank":0},"api_exboss_flag":0}]}"""
    val json = parse(jsonValue)
    val mapInfo = MapInfo.fromJson(json \ "api_data")
    val expected = List(
      MapInfo(11, true, false, None, None),
      MapInfo(12, true, false, None, None),
      MapInfo(13, true, false, None, None),
      MapInfo(14, true, false, None, None),
      MapInfo(15, false, true, Some(0), None),
      MapInfo(21, true, false, None, None),
      MapInfo(22, true, false, None, None),
      MapInfo(23, true, false, None, None),
      MapInfo(24, true, false, None, None),
      MapInfo(25, false, true, Some(0), None),
      MapInfo(31, true, false, None, None),
      MapInfo(32, true, false, None, None),
      MapInfo(33, true, false, None, None),
      MapInfo(34, true, false, None, None),
      MapInfo(35, false, true, Some(0), None),
      MapInfo(41, true, false, None, None),
      MapInfo(42, true, false, None, None),
      MapInfo(43, true, false, None, None),
      MapInfo(44, true, false, None, None),
      MapInfo(45, false, true, Some(0), None),
      MapInfo(51, true, false, None, None),
      MapInfo(52, true, false, None, None),
      MapInfo(53, true, false, None, None),
      MapInfo(54, true, false, None, None),
      MapInfo(55, false, true, Some(0), None),
      MapInfo(61, false, false, None, None),
      MapInfo(311, false, false, None, Some(EventMap(Some(Hp(9999, 9999)), 1, Some(0))))
    )
    assert(mapInfo === expected)
  }

  test("success pattern after start event") {
    val jsonValue = """{"api_result":1,"api_result_msg":"\u6210\u529f","api_data":[{"api_id":11,"api_cleared":1,"api_exboss_flag":0},{"api_id":12,"api_cleared":1,"api_exboss_flag":0},{"api_id":13,"api_cleared":1,"api_exboss_flag":0},{"api_id":14,"api_cleared":1,"api_exboss_flag":0},{"api_id":15,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":21,"api_cleared":1,"api_exboss_flag":0},{"api_id":22,"api_cleared":1,"api_exboss_flag":0},{"api_id":23,"api_cleared":1,"api_exboss_flag":0},{"api_id":24,"api_cleared":1,"api_exboss_flag":0},{"api_id":25,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":31,"api_cleared":1,"api_exboss_flag":0},{"api_id":32,"api_cleared":1,"api_exboss_flag":0},{"api_id":33,"api_cleared":1,"api_exboss_flag":0},{"api_id":34,"api_cleared":1,"api_exboss_flag":0},{"api_id":35,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":41,"api_cleared":1,"api_exboss_flag":0},{"api_id":42,"api_cleared":1,"api_exboss_flag":0},{"api_id":43,"api_cleared":1,"api_exboss_flag":0},{"api_id":44,"api_cleared":1,"api_exboss_flag":0},{"api_id":45,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":51,"api_cleared":1,"api_exboss_flag":0},{"api_id":52,"api_cleared":1,"api_exboss_flag":0},{"api_id":53,"api_cleared":1,"api_exboss_flag":0},{"api_id":54,"api_cleared":1,"api_exboss_flag":0},{"api_id":55,"api_cleared":0,"api_exboss_flag":1,"api_defeat_count":0},{"api_id":61,"api_cleared":0,"api_exboss_flag":0},{"api_id":321,"api_cleared":0,"api_eventmap":{"api_now_maphp":320,"api_max_maphp":400,"api_state":1,"api_selected_rank":1},"api_exboss_flag":0}]}"""
    val json = parse(jsonValue)
    val mapInfo = MapInfo.fromJson(json \ "api_data")
    assert(mapInfo.last === MapInfo(321, false, false, None, Some(EventMap(Some(Hp(320, 400)), 1, Some(1)))))
  }
}
