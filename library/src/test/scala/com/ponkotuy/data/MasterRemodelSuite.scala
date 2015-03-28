package com.ponkotuy.data

import com.ponkotuy.data.master.MasterRemodel
import com.ponkotuy.tool.PostQueryParser
import org.scalatest.FunSuite
import org.json4s.native.JsonMethods._

/**
 *
 * @author ponkotuy
 * Date: 15/02/05.
 */
class MasterRemodelSuite extends FunSuite {
  test("normal pattern") {
    val jsonValue = """{"api_result":1,"api_result_msg":"\u6210\u529f","api_data":{"api_req_buildkit":1,"api_req_remodelkit":1,"api_certain_buildkit":2,"api_certain_remodelkit":2,"api_req_slot_id":0,"api_req_slot_num":0,"api_change_flag":0}}"""
    val postValue = """api%5Fverno=1&api%5Fid=101&api%5Ftoken=xxxx&api%5Fslot%5Fid=764"""
    val value = MasterRemodel.fromJson(parse(jsonValue) \ "api_data", PostQueryParser.parse(postValue), List(0, 1, 2, 3, 4, 5))
    val expected = MasterRemodel(1, 1, 2, 2, 0, 0, false, 764, 1)
    assert(value.isDefined)
    assert(value.get === expected)
  }
}
