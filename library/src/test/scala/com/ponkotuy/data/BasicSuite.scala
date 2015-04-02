package com.ponkotuy.data

import org.scalatest.FunSuite
import org.json4s.native.JsonMethods._

/**
 * @author ponktouy
 * Date: 15/04/02.
 */
class BasicSuite extends FunSuite {
  test("no comment basic") {
    val jsonValue = """{"api_member_id":"10007732","api_nickname":"ぽんこつ","api_nickname_id":"110136878","api_active_flag":1,"api_starttime":1427967545673,"api_level":98,"api_rank":5,"api_experience":952964,"api_fleetname":null,"api_comment":"","api_comment_id":"","api_max_chara":100,"api_max_slotitem":497,"api_max_kagu":0,"api_playtime":0,"api_tutorial":0,"api_furniture":[4,64,76,123,157,168],"api_count_deck":4,"api_count_kdock":2,"api_count_ndock":4,"api_fcoin":10590,"api_st_win":4013,"api_st_lose":129,"api_ms_count":1127,"api_ms_success":1114,"api_pt_win":1235,"api_pt_lose":101,"api_pt_challenged":0,"api_pt_challenged_win":0,"api_firstflag":1,"api_tutorial_progress":100,"api_pvp":[0,0],"api_medals":0,"api_large_dock":1}"""
    val expected = Basic(98, 952964, 5, 100, 10590, 4013, 129, 1127, 1114, 1235, 101, 0, "", 4, 2, 4, true)
    val basic = Basic.fromJSON(parse(jsonValue))
    assert(basic === expected)
  }
}
