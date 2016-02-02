package ranking.common

import java.util.Locale

import ranking._

/**
 *
 * @author ponkotuy
 * Date: 15/02/26.
 */
sealed abstract class RankingType {
  def rankings: Array[Ranking]
}

object RankingType {
  case object Admiral extends RankingType {
    override val rankings: Array[Ranking] = Array(
      MaterialRanking,
      FirstShipRanking,
      LuckRanking,
      ShipBookRanking,
      MarriedRanking,
      SumShipExpRanking,
      ItemBookRanking,
      RemodelRanking,
      ScoreRanking,
      LastScoreRanking
    )
  }

  case object Ship extends RankingType {
    override val rankings: Array[Ranking] = Array(
      ExpByShipRanking,
      YomeByShipRanking,
      MarriedByShipRanking,
      FirstShipRate,
      RareShipRanking
    )
  }

  val values = Array(Admiral, Ship)

  def fromStr(str: String): Option[RankingType] =
    values.find(_.toString.toLowerCase(Locale.US) == str.toLowerCase(Locale.US))
}
