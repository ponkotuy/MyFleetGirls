package com.ponkotuy.data

import com.ponkotuy.tool.PostQueryParser
import org.scalatest.FunSuite
import org.json4s.native.JsonMethods._

/**
 *
 * @author ponkotuy
 * Date: 15/01/31.
 */
class RemodelSuite extends FunSuite {
  test("success pattern") {
    val jsonValue = """{"api_result":1,"api_result_msg":"成功","api_data":{"api_remodel_flag":1,"api_remodel_id":[2,2],"api_after_material":[25578,26962,22173,34490,1425,1564,2183,9],"api_voice_id":"0","api_after_slot":{"api_id":46284,"api_slotitem_id":2,"api_locked":1,"api_level":5}}}"""
    val postValue = """api%5Fslot%5Fid=46284&api%5Fid=101&api%5Fverno=1&api%5Ftoken=&api%5Fcertain%5Fflag=0"""
    val expected = Remodel(true, 2, 2, 0, Some(RemodelAfterSlot(46284, 2, true, 5)), Nil, false, 46284, Some(1), None)
    common(jsonValue, postValue, expected)
  }

  test("success pattern with consume slotitem") {
    val jsonValue = """{"api_result":1,"api_result_msg":"成功","api_data":{"api_remodel_flag":1,"api_remodel_id":[2,2],"api_after_material":[28284,28787,24592,37207,1419,1554,2172,10],"api_voice_id":"0","api_after_slot":{"api_id":46185,"api_slotitem_id":2,"api_locked":1,"api_level":9},"api_use_slot_id":[48952]}}"""
    val postValue = """api%5Fslot%5Fid=46185&api%5Fverno=1&api%5Fcertain%5Fflag=0&api%5Fid=101&api%5Ftoken="""
    val expected = Remodel(true, 2, 2, 0, Some(RemodelAfterSlot(46185, 2, true, 9)), List(48952), false, 46185, Some(1), None)
    common(jsonValue, postValue, expected)
  }

  test("success pattern with powerup slotitem") {
    val jsonValue = """{"api_result":1,"api_result_msg":"成功","api_data":{"api_remodel_flag":1,"api_remodel_id":[120,121],"api_after_material":[25292,25570,25244,41113,1405,1525,2137,8],"api_voice_id":"10","api_after_slot":{"api_id":46594,"api_slotitem_id":121,"api_locked":0,"api_level":0},"api_use_slot_id":[198,1642]}}"""
    val postValue = """api%5Fslot%5Fid=46594&api%5Fverno=1&api%5Fcertain%5Fflag=1&api%5Fid=104&api%5Ftoken="""
    val expected = Remodel(true, 120, 121, 10, Some(RemodelAfterSlot(46594, 121, false, 0)), List(198, 1642), true, 46594, Some(1), None)
    common(jsonValue, postValue, expected)
  }

  test("failure pattern") {
    val jsonValue = """{"api_result":1,"api_result_msg":"成功","api_data":{"api_remodel_flag":0,"api_remodel_id":[2,2],"api_after_material":[23387,25355,17099,31296,1428,1570,2187,9],"api_voice_id":"0"}}"""
    val postValue = """api%5Fcertain%5Fflag=0&api%5Fverno=1&api%5Fslot%5Fid=46284&api%5Fid=101&api%5Ftoken="""
    val expected = Remodel(false, 2, 2, 0, None, Nil, false, 46284, Some(1), None)
    common(jsonValue, postValue, expected)
  }

  def common(json: String, post: String, expected: Remodel): Unit = {
    val jsonValue = parse(json)
    val postValue = PostQueryParser.parse(post)
    val value = Remodel.fromJson(jsonValue \ "api_data", postValue, 1, None)
    assert(value.isDefined)
    assert(value.get === expected)
  }
}
