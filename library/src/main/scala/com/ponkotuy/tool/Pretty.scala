package com.ponkotuy.tool

/**
 *
 * @author ponkotuy
 * Date: 14/04/22.
 */
object Pretty {
  def apply(map: Map[String, Any]): String = {
    map.map { case (k, v) => s"$k -> $v" }.mkString(", ")
  }
}
