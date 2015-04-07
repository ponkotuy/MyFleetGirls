package models.db

import com.ponkotuy.tool.DiffCalc
import scalikejdbc._
import com.ponkotuy.data

/**
 *
 * @param instant : Instant Construction
 * @param develop : Development Material
 * @param revamping : Revamping(Upgrade) Material
 * @author ponkotuy
 * Date: 14/02/19.
 */
case class Material(
    id: Long, memberId: Long,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    instant: Int, bucket: Int, develop: Int, revamping: Int,
    created: Long) {
  def save()(implicit session: DBSession = Material.autoSession): Material =
    Material.save(this)

  def diff(x: data.Material): Double = {
    import DiffCalc._
    Iterator(
      diffRatio(10000.0)(fuel, x.fuel),
      diffRatio(10000.0)(ammo, x.ammo),
      diffRatio(10000.0)(steel, x.steel),
      diffRatio(10000.0)(bauxite, x.bauxite),
      diffRatio(100.0)(instant, x.instant),
      diffRatio(100.0)(bucket, x.bucket),
      diffRatio(100.0)(develop, x.develop),
      diffRatio(100.0)(revamping, x.revamping)
    ).sum
  }

  def destroy()(implicit session: DBSession = Material.autoSession): Unit = Material.destroy(id)(session)
}

object Material extends SQLSyntaxSupport[Material] {
  lazy val m = Material.syntax("m")

  def apply(m: SyntaxProvider[Material])(rs: WrappedResultSet): Material = apply(m.resultName)(rs)
  def apply(m: ResultName[Material])(rs: WrappedResultSet): Material = autoConstruct(rs, m)

  def save(m: Material)(implicit session: DBSession = Material.autoSession): Material = {
    withSQL {
      update(Material).set(
        column.memberId -> m.memberId,
        column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
        column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop, column.revamping -> m.revamping
      )
    }.update()
    m
  }

  def create(m: data.Material, memberId: Long)(
    implicit session: DBSession = Material.autoSession): Long = {
    val created = System.currentTimeMillis()
    withSQL {
      insert.into(Material).namedValues(
        column.memberId -> memberId,
        column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
        column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop, column.revamping -> m.revamping,
        column.created -> created
      )
    }.updateAndReturnGeneratedKey().apply()
  }

  /** 指定ユーザの最新1件を取ってくる
    */
  def findByUser(memberId: Long)(implicit session: DBSession = Material.autoSession): Option[Material] = withSQL {
    select.from(Material as m)
      .where.eq(m.memberId, memberId)
      .orderBy(m.created).desc
      .limit(1)
  }.map(Material(m)).toOption().apply()

  def findAllByUser(memberId: Long, from: Long = 0, to: Long = Long.MaxValue)(
      implicit session: DBSession = autoSession): List[Material] = {
    withSQL {
      select.from(Material as m)
        .where.eq(m.memberId, memberId).and.between(m.created, from, to)
        .orderBy(m.created).asc
    }.map(Material(m)).list().apply()
  }

  def destroy(id: Long)(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(Material).where.eq(column.id, id)
  }
}
