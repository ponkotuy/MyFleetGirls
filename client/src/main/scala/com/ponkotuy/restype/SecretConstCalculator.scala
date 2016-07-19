package com.ponkotuy.restype

/**
  * values = xs.map(_ * C) (1 <= C <= 9) のときのCを推定する
  */
class SecretConstCalculator(values: Set[Int] = Set.empty) {
  def add(others: Set[Int]) = new SecretConstCalculator(values ++ others)
  lazy val calc: Option[Int] = {
    if(values.size < 2) None
    else {
      Range(9, 0, -1).find { i =>
        values.forall(_ % i == 0)
      }
    }
  }
}
