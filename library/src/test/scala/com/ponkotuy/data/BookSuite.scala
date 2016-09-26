package com.ponkotuy.data

import org.scalatest.FunSuite
import org.json4s.native.JsonMethods._

/**
 *
 * @author ponkotuy
 * Date: 10/22/15.
 */
class BookSuite extends FunSuite {
  test("success ship_book(page1)") {
    val stream = getClass.getResource("/ship_book.json").openStream()
    val values = try {
      Book.fromJson(parse(stream) \ "api_data")
    } finally {
      stream.close()
    }
    assert(values.nonEmpty)
    assert(values.forall(_.isInstanceOf[ShipBook]))
  }

  // Nullに対応できる
  test("success ship_book(page5)") {
    val jsonValue = """{"api_result":1,"api_result_msg":"\u6210\u529f","api_data":null}"""
    val values = Book.fromJson(parse(jsonValue) \ "api_data")
    assert(values.isEmpty)
  }

  test("success item_book") {
    val stream = getClass.getResource("/item_book.json").openStream()
    val values = try {
      Book.fromJson(parse(stream) \ "api_data")
    } finally {
      stream.close()
    }
    assert(values.nonEmpty)
    assert(values.forall(_.isInstanceOf[ItemBook]))
  }
}
