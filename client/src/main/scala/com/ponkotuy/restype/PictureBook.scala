package com.ponkotuy.restype

import com.ponkotuy.data.{Book, ItemBook, ShipBook}
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object PictureBook extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/picture_book\\z".r

  override def postables(q: Query): Seq[Result] = {
    val books = Book.fromJson(q.obj)
    if(books.isEmpty) Nil
    else {
      val result = books.head match {
        case _: ShipBook => NormalPostable("/book/ship", write(books))
        case _: ItemBook => NormalPostable("/book/item", write(books))
      }
      result :: Nil
    }
  }
}
