package models.db

import com.ponkotuy.data.master
import scalikejdbc._
import util.ehcache.TimeToLiveCache
import util.scalikejdbc.BulkInsert._

import scala.concurrent.duration._

case class MasterShipSpecs(
  id: Int,
  hp: Int,
  soukoMin: Int,
  soukoMax: Int,
  karyokuMin: Int,
  karyokuMax: Int,
  raisouMin: Int,
  raisouMax: Int,
  taikuMin: Int,
  taikuMax: Int,
  luckyMin: Int,
  luckyMax: Int,
  soku: Int,
  length: Int,
  maxeq: Seq[Int]) {

  def save()(implicit session: DBSession = MasterShipSpecs.autoSession): MasterShipSpecs = MasterShipSpecs.save(this)(session)

  def destroy()(implicit session: DBSession = MasterShipSpecs.autoSession): Unit = MasterShipSpecs.destroy(this)(session)

}


object MasterShipSpecs extends SQLSyntaxSupport[MasterShipSpecs] {

  val cache = MasterShipSpecsCache

  override val tableName = "master_ship_specs"

  override val columns = Seq("id", "hp", "souko_min", "souko_max", "karyoku_min", "karyoku_max", "raisou_min", "raisou_max", "taiku_min", "taiku_max", "lucky_min", "lucky_max", "soku", "length", "maxeq")

  def apply(mss: SyntaxProvider[MasterShipSpecs])(rs: WrappedResultSet): MasterShipSpecs = apply(mss.resultName)(rs)
  def apply(mss: ResultName[MasterShipSpecs])(rs: WrappedResultSet): MasterShipSpecs = new MasterShipSpecs(
    id = rs.int(mss.id),
    hp = rs.int(mss.hp),
    soukoMin = rs.int(mss.soukoMin),
    soukoMax = rs.int(mss.soukoMax),
    karyokuMin = rs.int(mss.karyokuMin),
    karyokuMax = rs.int(mss.karyokuMax),
    raisouMin = rs.int(mss.raisouMin),
    raisouMax = rs.int(mss.raisouMax),
    taikuMin = rs.int(mss.taikuMin),
    taikuMax = rs.int(mss.taikuMax),
    luckyMin = rs.int(mss.luckyMin),
    luckyMax = rs.int(mss.luckyMax),
    soku = rs.int(mss.soku),
    length = rs.int(mss.length),
    maxeq = rs.string(mss.maxeq).split(',').map(_.toInt)
  )

  val mss = MasterShipSpecs.syntax("mss")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[MasterShipSpecs] = cache.get(id)

  private[db] def findDb(id: Int)(implicit session: DBSession = autoSession): Option[MasterShipSpecs] = withSQL {
    select.from(MasterShipSpecs as mss).where.eq(mss.id, id)
  }.map(MasterShipSpecs(mss.resultName)).single().apply()

  def findAll()(implicit session: DBSession = autoSession): List[MasterShipSpecs] = {
    withSQL(select.from(MasterShipSpecs as mss)).map(MasterShipSpecs(mss.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MasterShipSpecs as mss)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterShipSpecs] = {
    withSQL {
      select.from(MasterShipSpecs as mss).where.append(sqls"${where}")
    }.map(MasterShipSpecs(mss.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MasterShipSpecs as mss).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    id: Int,
    hp: Int,
    soukoMin: Int,
    soukoMax: Int,
    karyokuMin: Int,
    karyokuMax: Int,
    raisouMin: Int,
    raisouMax: Int,
    taikuMin: Int,
    taikuMax: Int,
    luckyMin: Int,
    luckyMax: Int,
    soku: Int,
    length: Int,
    maxeq: Seq[Int])(implicit session: DBSession = autoSession): MasterShipSpecs = {
    withSQL {
      insert.into(MasterShipSpecs).columns(
        column.id,
        column.hp,
        column.soukoMin,
        column.soukoMax,
        column.karyokuMin,
        column.karyokuMax,
        column.raisouMin,
        column.raisouMax,
        column.taikuMin,
        column.taikuMax,
        column.luckyMin,
        column.luckyMax,
        column.soku,
        column.length,
        column.maxeq
      ).values(
          id,
          hp,
          soukoMin,
          soukoMax,
          karyokuMin,
          karyokuMax,
          raisouMin,
          raisouMax,
          taikuMin,
          taikuMax,
          luckyMin,
          luckyMax,
          soku,
          length,
          maxeq.mkString(",")
        )
    }.update().apply()

    MasterShipSpecs(
      id = id,
      hp = hp,
      soukoMin = soukoMin,
      soukoMax = soukoMax,
      karyokuMin = karyokuMin,
      karyokuMax = karyokuMax,
      raisouMin = raisouMin,
      raisouMax = raisouMax,
      taikuMin = taikuMin,
      taikuMax = taikuMax,
      luckyMin = luckyMin,
      luckyMax = luckyMax,
      soku = soku,
      length = length,
      maxeq = maxeq)
  }

  def bulkInsert(xs: Seq[master.MasterShipSpecs])(implicit session: DBSession = autoSession): Unit = {
    require(xs.nonEmpty)
    applyUpdate {
      insert.into(MasterShipSpecs)
        .columns(
          column.id, column.hp, column.soukoMin, column.soukoMax, column.karyokuMin, column.karyokuMax,
          column.raisouMin, column.raisouMax, column.taikuMin, column.taikuMax, column.luckyMin, column.luckyMax,
          column.soku, column.length, column.maxeq
        )
        .multiValues(
          xs.map(_.id), xs.map(_.hp), xs.map(_.soukoMin), xs.map(_.soukoMax),
          xs.map(_.karyokuMin), xs.map(_.karyokuMax), xs.map(_.raisouMin), xs.map(_.raisouMax),
          xs.map(_.taikuMin), xs.map(_.taikuMax),
          xs.map(_.luckyMin), xs.map(_.luckyMax), xs.map(_.soku), xs.map(_.length), xs.map(_.maxeq.mkString(","))
        )
    }
  }

  def save(entity: MasterShipSpecs)(implicit session: DBSession = autoSession): MasterShipSpecs = {
    withSQL {
      update(MasterShipSpecs).set(
        column.id -> entity.id,
        column.hp -> entity.hp,
        column.soukoMin -> entity.soukoMin,
        column.soukoMax -> entity.soukoMax,
        column.karyokuMin -> entity.karyokuMin,
        column.karyokuMax -> entity.karyokuMax,
        column.raisouMin -> entity.raisouMin,
        column.raisouMax -> entity.raisouMax,
        column.taikuMin -> entity.taikuMin,
        column.taikuMax -> entity.taikuMax,
        column.luckyMin -> entity.luckyMin,
        column.luckyMax -> entity.luckyMax,
        column.soku -> entity.soku,
        column.length -> entity.length,
        column.maxeq -> entity.maxeq.mkString(",")
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: MasterShipSpecs)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MasterShipSpecs).where.eq(column.id, entity.id)
    }.update().apply()
  }

  def deleteAll()(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MasterShipSpecs)
    }.update().apply()
  }

}

object MasterShipSpecsCache extends TimeToLiveCache[Int, MasterShipSpecs] {
  override def cacheName: String = "masterShipSpecs"
  override def maxEntries: Int = 1000
  override def liveSeconds: Long = 60.minutes.toSeconds
  override protected def default(k: Int): Option[MasterShipSpecs] = MasterShipSpecs.findDb(k)
}
