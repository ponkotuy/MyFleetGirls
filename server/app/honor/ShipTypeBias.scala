package honor

import tool.STypeExp

/**
 *
 * @author ponkotuy
 * Date: 15/03/18.
 */
object ShipTypeBias extends HonorCategory {
  override def category: Int = 8

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val ships = db.shipWithName
    val result = STypeExp.fromShips(ships).maxBy(_.exp)
    s"${result.name}提督" :: OriginalHonors.get(result.name).toList
  }

  val OriginalHonors = Map(
    "駆逐艦" -> "ロリコン提督",
    "戦艦" -> "大艦巨砲主義",
    "正規空母" -> "アウトレンジ戦法",
    "重雷装巡洋艦" -> "超重雷撃",
    "潜水艦" -> "オリョクル提督"
  )

  override val comment: String = "各ユーザ統計の経験値取得比率が最も高い艦種"
}
