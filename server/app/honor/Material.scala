package honor

import models.db

/**
 * Date: 15/05/19.
 *
 * @author ponkotuy
 */
object Material extends HonorCategory {
  override def category: Int = 19

  override def comment: String = "資源を一定以上(以下)にする"

  override def approved(memberId: Long): List[String] = {
    db.Material.findByUser(memberId).map { material =>
      conditions.filter(_.cond(material)).map(_.name)
    }.getOrElse(Nil)
  }

  case class Condition(cond: db.Material => Boolean, name: String)

  val Lack = 1000
  val Rich = 200000

  val conditions: List[Condition] = List(
    Condition(_.ammo < Lack, "弾薬欠乏"),
    Condition(_.ammo > Rich, "弾薬王"),
    Condition(_.fuel < Lack, "燃料欠乏"),
    Condition(_.fuel < Lack, "石油の一滴血の一滴"),
    Condition(_.fuel > Rich, "石油王"),
    Condition(_.steel < Lack, "鉄不足"),
    Condition(_.steel > Rich, "鉄鋼王国"),
    Condition(_.bauxite < Lack, "ボーキサイト不足"),
    Condition(_.bauxite > Rich, "アルミ王国"),
    Condition(m => (m.ammo + m.fuel + m.steel + m.bauxite) > (Rich * 3), "米帝"),
    Condition(m => (m.ammo + m.fuel + m.steel + m.bauxite) > (Rich * 3), "贅沢は素敵だ"),
    Condition(m => (m.ammo + m.fuel + m.steel + m.bauxite) < (Lack * 3), "贅沢は敵だ")
  )
}
