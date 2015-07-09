package honor

import models.db.{MasterShipBase, ShipBook}
import ranking.EvolutionBase
import scalikejdbc._

import scala.collection.breakOut

/**
 * ○○が出ないんです！って言いたいとき用称号
 *
 * ここのリストに入る艦娘の条件は
 *
 * 1. かなりレアである
 * 2. 通常建造では出ない
 * 3. 大型建造または通常海域のボスでのみ出現
 *
 * 入手がイベントのみの場合は非対象。
 * 上記条件を満たさなくなったものは削除されるべき（だがどうせ気付かないだろうなーという本心が
 *
 * @author ponkotuy
 * Date: 15/04/16.
 */
case object NotHave extends HonorCategory {
  import tool.ShipIds._
  override def category: Int = 16

  override def comment: String = "持たざるものには分かる"

  override def approved(memberId: Long): List[String] = {
    val shipIds = ShipBook.findAllBy(sqls.eq(ShipBook.sb.memberId, memberId)).map(_.id)
    val haves: Set[Int] = shipIds.map(EvolutionBase(_))(breakOut)
    MasterShipBase.findAllBy(sqls.in(MasterShipBase.ms.id, (Target -- haves).toSeq)).map { ship =>
      s"${ship.name}出ない"
    }
  }

  val Target = Set(
    Yamato, Noshiro, Sakawa, Musashi, Taiho, AkitsuMaru, Bismarck, Taigei,
    Harusame, Katori, Unryu, I401, Mikuma, Hatsukaze, Yahagi, Hayashimo, Kiyoshimo, Asashimo, Nowaki, Asagumo, Ooyodo, Tanikaze
  )
}
