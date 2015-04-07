package com.ponkotuy.tool

/**
 *
 * @author ponkotuy
 * Date: 15/04/07.
 */
trait RankingDiff {
  def no: Int
  def rate: Int

  def diff(x: RankingDiff): Double = {
    import DiffCalc._
    Iterator(
      neq(no, x.no),
      neq(rate, x.rate)
    ).max
  }
}
