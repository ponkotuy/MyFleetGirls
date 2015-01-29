package ranking.common

/**
 * @author ponkotuy
 * Date: 15/01/29.
 */
case class CachedRanking(ranking: Ranking) extends Ranking {
  override def title: String = ranking.title
  override def comment: List[String] = ranking.comment
  override def divClass: String = ranking.divClass

  val cache = new RankingElementCache(ranking.getClass.getName)

  override def rankingQuery(limit: Int): List[RankingElement] = {
    if(limit <= cache.getSize) {
      val result = (0 until limit).flatMap(cache.get).toList
      if(result.size == limit) result else updateAndReturn(limit)
    } else updateAndReturn(limit)
  }

  private[this] def updateAndReturn(limit: Int): List[RankingElement] = {
    val result = ranking.rankingQuery(limit)
    val map = result.zipWithIndex.map { case (elem, i) => i -> elem }.toMap
    cache.putAll(map)
    result
  }
}
