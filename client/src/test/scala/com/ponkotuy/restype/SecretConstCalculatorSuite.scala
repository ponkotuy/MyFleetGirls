package com.ponkotuy.restype

import org.scalatest.FunSuite

class SecretConstCalculatorSuite extends FunSuite {
  test("calc is returned secret const") {
    val secretConst = 3
    val seq = Seq(1, 2, 3, 4, 5).map(_ * secretConst)
    val calculator = new SecretConstCalculator(seq.toSet)
    assert(calculator.calc === Some(secretConst))
  }

  test("use add") {
    val secretConst = 3
    val seqs = Seq(Seq(2, 4), Seq(3), Seq(6, 9)).map(_.map(_ * secretConst))
    val calculator = genCalculator(seqs)
    assert(calculator.calc === Some(secretConst))
  }

  test("calc is not returned if use only a value") {
    val seqs = Seq(Seq(5, 5, 5), Seq(5, 5))
    val calculator = genCalculator(seqs)
    assert(calculator.calc === None)
  }

  private[this] def genCalculator(seqs: Seq[Seq[Int]]) =
    seqs.foldLeft(new SecretConstCalculator()) { (calculator, seq) => calculator.add(seq.toSet) }
}
