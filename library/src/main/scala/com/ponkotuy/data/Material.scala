package com.ponkotuy.data

/**
 *
 * @param instant : Instant Construction
 * @param develop : Development Material
 * @author ponkotuy
 * Date: 14/02/19.
 */
case class Material(fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    instant: Int, bucket: Int, develop: Int)

object Material {
  def fromSeq(s: Seq[Int]): Material = {
    Material(s(0), s(1), s(2), s(3), s(4), s(5), s(6))
  }
}
