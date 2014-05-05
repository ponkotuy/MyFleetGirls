package dat

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

/**
 *
 * @author ponkotuy
 * Date: 14/05/05.
 */
trait GraphData {
  def karyoku: Int
  def raisou: Int
  def taiku: Int
  def soukou: Int
  def kaihi: Int
  def taisen: Int
  def sakuteki: Int
  def lucky: Int

  def elems = Seq(
    GraphElem("火力", karyoku, 150),
    GraphElem("雷装", raisou, 150),
    GraphElem("対空", taiku, 100),
    GraphElem("装甲", soukou, 100),
    GraphElem("回避", kaihi, 100),
    GraphElem("対潜", taisen, 100),
    GraphElem("索敵", sakuteki, 100),
    GraphElem("運", lucky, 50)
  )

//  def toJson: String = toFlot // 互換性維持

  def toFlot: String = {
    val seq = elems.zipWithIndex.map { case (elem, i) =>
      Seq(i, elem.rate)
    }
    compact(render(seq))
  }

  def toJqPlot: String = {
    val seq = elems.map { case elem =>
      Seq(JDouble(elem.percent), JString(elem.name), JInt(elem.raw))
    }.reverse
    compact(render(Seq(seq)))
  }
}

case class GraphElem(name: String, raw: Int, max: Double) {
  def rate: Double = raw / max
  def percent: Double = rate * 100.0
}
