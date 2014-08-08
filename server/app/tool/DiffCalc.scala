package tool

/** 新規挿入の判断等に使う差分情報の計算ツール
  *
  * 0,0は差無し、0.0以上の値で差の大きさを示す
  *
  * @author ponkotuy
  * Date: 14/02/28.
  */
object DiffCalc {
  def ratio[T <% Double](x: T, y: T): Double =
    (if(x > y) x / y else y / x) - 1.0

  /** 差のbaseからの比を取る */
  def diffRatio[T <% Double](base: Double)(x: T, y: T): Double =
    math.abs(x - y) / base

  def neq(x: Any, y: Any): Double =
    if(x == y) 0.0 else Double.MaxValue
}
