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
    Book.fromJson(q.obj) match {
      case books@(_: ShipBook) :: _ =>
        NormalPostable("/book/ship", write(books)) :: Nil
      case books@(_: ItemBook) :: _ =>
        NormalPostable("/book/item", write(books)) :: Nil
      case _ =>
        Nil
    }
  }
}
