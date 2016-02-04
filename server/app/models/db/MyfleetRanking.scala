package models.db

import ranking.common.RankingElement
import scalikejdbc._
import org.json4s._
import org.json4s.native.JsonMethods._
import util.Ymdh

case class MyfleetRanking(
  id: Long,
  rankingId: Int,
  rank: Int,
  yyyymmddhh: Int,
  targetId: Long,
  targetName: String,
  data: String,
  url: Option[String] = None,
  num: Long,
  created: Long) {

  def save()(implicit session: DBSession = MyfleetRanking.autoSession): MyfleetRanking = MyfleetRanking.save(this)(session)

  def destroy()(implicit session: DBSession = MyfleetRanking.autoSession): Unit = MyfleetRanking.destroy(this)(session)

  def toRankingElement: Option[RankingElement] = {
    for {
      rank <- ranking
      parsed <- parseOpt(data)
      decoded <- rank.decodeData(parsed)
    } yield {
      RankingElement(targetId, targetName, decoded, url, num)
    }
  }

  lazy val ranking: Option[_root_.ranking.common.Ranking] = _root_.ranking.common.Ranking.fromId(rankingId)
}


object MyfleetRanking extends SQLSyntaxSupport[MyfleetRanking] {

  override val tableName = "myfleet_ranking"

  override val columns = Seq("id", "ranking_id", "rank", "yyyymmddhh", "target_id", "target_name", "data", "url", "num", "created")

  def apply(mr: SyntaxProvider[MyfleetRanking])(rs: WrappedResultSet): MyfleetRanking = autoConstruct(rs, mr)
  def apply(mr: ResultName[MyfleetRanking])(rs: WrappedResultSet): MyfleetRanking = autoConstruct(rs, mr)

  val mr = MyfleetRanking.syntax("mr")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[MyfleetRanking] = {
    withSQL {
      select.from(MyfleetRanking as mr).where.eq(mr.id, id)
    }.map(MyfleetRanking(mr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MyfleetRanking] = {
    withSQL(select.from(MyfleetRanking as mr)).map(MyfleetRanking(mr.resultName)).list.apply()
  }

  def findNewestTime()(implicit session: DBSession = autoSession): Option[Ymdh] = {
    withSQL {
      select(mr.yyyymmddhh).from(MyfleetRanking as mr).orderBy(mr.yyyymmddhh.desc).limit(1)
    }.map { rs => Ymdh.fromInt(rs.int(1)) }.single().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(MyfleetRanking as mr)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[MyfleetRanking] = {
    withSQL {
      select.from(MyfleetRanking as mr).where.append(where)
    }.map(MyfleetRanking(mr.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MyfleetRanking] = {
    withSQL {
      select.from(MyfleetRanking as mr).where.append(where)
    }.map(MyfleetRanking(mr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(MyfleetRanking as mr).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    rankingId: Int,
    rank: Int,
    yyyymmddhh: Int,
    targetId: Long,
    targetName: String,
    data: String,
    url: Option[String] = None,
    num: Long,
    created: Long)(implicit session: DBSession = autoSession): MyfleetRanking = {
    val generatedKey = withSQL {
      insert.into(MyfleetRanking).columns(
        column.rankingId,
        column.rank,
        column.yyyymmddhh,
        column.targetId,
        column.targetName,
        column.data,
        column.url,
        column.num,
        column.created
      ).values(
        rankingId,
        rank,
        yyyymmddhh,
        targetId,
        targetName,
        data,
        url,
        num,
        created
      )
    }.updateAndReturnGeneratedKey.apply()

    MyfleetRanking(
      id = generatedKey,
      rankingId = rankingId,
      rank = rank,
      yyyymmddhh = yyyymmddhh,
      targetId = targetId,
      targetName = targetName,
      data = data,
      url = url,
      num = num,
      created = created)
  }

  def batchInsert(entities: Seq[MyfleetRanking])(implicit session: DBSession = autoSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'rankingId -> entity.rankingId,
        'rank -> entity.rank,
        'yyyymmddhh -> entity.yyyymmddhh,
        'targetId -> entity.targetId,
        'targetName -> entity.targetName,
        'data -> entity.data,
        'url -> entity.url,
        'num -> entity.num,
        'created -> entity.created))
        SQL("""insert into myfleet_ranking(
        ranking_id,
        rank,
        yyyymmddhh,
        target_id,
        target_name,
        data,
        url,
        num,
        created
      ) values (
        {rankingId},
        {rank},
        {yyyymmddhh},
        {targetId},
        {targetName},
        {data},
        {url},
        {num},
        {created}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: MyfleetRanking)(implicit session: DBSession = autoSession): MyfleetRanking = {
    withSQL {
      update(MyfleetRanking).set(
        column.id -> entity.id,
        column.rankingId -> entity.rankingId,
        column.rank -> entity.rank,
        column.yyyymmddhh -> entity.yyyymmddhh,
        column.targetId -> entity.targetId,
        column.targetName -> entity.targetName,
        column.data -> entity.data,
        column.url -> entity.url,
        column.num -> entity.num,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: MyfleetRanking)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(MyfleetRanking).where.eq(column.id, entity.id) }.update.apply()
  }

}
