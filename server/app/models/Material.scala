package models

import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.{DBSession, WrappedResultSet}
import tool.DiffCalc

/**
 *
 * @param instant : Instant Construction
 * @param develop : Development Material
 * @author ponkotuy
 * Date: 14/02/19.
 */
case class Material(
    id: Long, memberId: Long,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    instant: Int, bucket: Int, develop: Int,
    created: Long) {
  def save()(implicit session: DBSession = Material.autoSession): Material =
    Material.save(this)

  def diff(x: data.Material): Double = {
    import DiffCalc._
    Iterator(
      ratio(fuel, x.fuel),
      ratio(ammo, x.ammo),
      ratio(steel, x.steel),
      ratio(bauxite, x.bauxite),
      ratio(instant, x.instant),
      ratio(bucket, x.bucket),
      ratio(develop, x.develop)
    ).max
  }
}

object Material extends SQLSyntaxSupport[Material] {
  lazy val m = Material.syntax("m")

  def apply(m: SyntaxProvider[Material])(rs: WrappedResultSet): Material = apply(m.resultName)(rs)
  def apply(m: ResultName[Material])(rs: WrappedResultSet): Material = new Material(
    id = rs.long(m.id),
    memberId = rs.long(m.memberId),
    fuel = rs.int(m.fuel),
    ammo = rs.int(m.ammo),
    steel = rs.int(m.steel),
    bauxite = rs.int(m.bauxite),
    instant = rs.int(m.instant),
    bucket = rs.int(m.bucket),
    develop = rs.int(m.develop),
    created = rs.long(m.created)
  )

  def save(m: Material)(implicit session: DBSession = Material.autoSession): Material = {
    withSQL {
      update(Material).set(
        column.memberId -> m.memberId,
        column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
        column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop
      )
    }.update()
    m
  }

  def create(m: data.Material)(
    implicit session: DBSession = Material.autoSession): Material = {
    val created = System.currentTimeMillis()
    val id = withSQL {
      insert.into(Material).namedValues(
        column.memberId -> m.memberId,
        column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
        column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop,
        column.created -> created
      )
    }.updateAndReturnGeneratedKey().apply()
    Material(id, m.memberId, m.fuel, m.ammo, m.steel, m.bauxite, m.instant, m.bucket, m.develop, created)
  }

  /** 指定ユーザの最新1件を取ってくる
    */
  def findByUser(memberId: Long)(implicit session: DBSession = Material.autoSession): Option[Material] = withSQL {
    select.from(Material as m)
      .where.eq(m.memberId, memberId)
      .orderBy(m.created).desc
      .limit(1)
  }.map(Material(m)).toOption().apply()

  def findAllByUser(memberId: Long)(implicit session: DBSession = Material.autoSession): List[Material] = withSQL {
    select.from(Material as m)
      .where.eq(m.memberId, memberId)
      .orderBy(m.created).desc
  }.map(Material(m)).list().apply()
}
