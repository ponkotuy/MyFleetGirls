package ranking.common

/**
 * @author ponkotuy
 * Date: 15/01/29.
 */
case class CachedRanking(ranking: Ranking) extends Ranking {
  override def title = ranking.title
  override def comment = ranking.comment
  override def divClass = ranking.divClass

  val cache = new RankingElementCache(ranking.getClass.getName)

  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    if(limit <= cache.getSize) {
      val result = (0 until limit).flatMap(cache.get).toList
      if(result.size == limit) result else updateAndReturn(limit)
    } else updateAndReturn(limit)
  }

  private[this] def updateAndReturn(limit: Int): Seq[RankingElement] = {
    val result = ranking.rankingQuery(limit)
    val map = result.zipWithIndex.map { case (elem, i) => i -> elem }.toMap
    cache.putAll(map)
    result
  }
}
