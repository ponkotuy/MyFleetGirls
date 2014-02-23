package models

import scala.concurrent.Future
import scalikejdbc.async._
import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.WrappedResultSet

/**
 *
 * @param instant : Instant Construction
 * @param develop : Development Material
 * @author ponkotuy
 * Date: 14/02/19.
 */
case class Material(
    id: Long, userId: Long,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    instant: Int, bucket: Int, develop: Int,
    created: Long) extends ShortenedNames {
  def save()(implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Material] =
    Material.save(this)
}

object Material extends SQLSyntaxSupport[Material] with ShortenedNames {
  lazy val m = Material.syntax("m")

  def apply(m: SyntaxProvider[Material])(rs: WrappedResultSet): Material = apply(m.resultName)(rs)
  def apply(m: ResultName[Material])(rs: WrappedResultSet): Material = new Material(
    id = rs.long(m.id),
    userId = rs.long(m.userId),
    fuel = rs.int(m.fuel),
    ammo = rs.int(m.ammo),
    steel = rs.int(m.steel),
    bauxite = rs.int(m.bauxite),
    instant = rs.int(m.instant),
    bucket = rs.int(m.bucket),
    develop = rs.int(m.develop),
    created = rs.long(m.created)
  )

  def save(m: Material)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Material] = withSQL {
    update(Material).set(
      column.userId -> m.userId,
      column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
      column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop
    )
  }.update().future.map(_ => m)

  def create(m: data.Material, userId: Long)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Material] = {
    val created = System.currentTimeMillis()
    val idFuture = withSQL {
      insert.into(Material).namedValues(
        column.userId -> userId,
        column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
        column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop,
        column.created -> created
      )
    }.updateAndReturnGeneratedKey().future()
    idFuture.map { id =>
      Material(id, userId, m.fuel, m.ammo, m.steel, m.bauxite, m.instant, m.bucket, m.develop, created)
    }
  }

  def findAllByUser(userId: Long)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[List[Material]] = withSQL {
    select.from(Material as m)
      .where.eq(m.userId, userId)
      .orderBy(m.created).desc
  }.map(Material(m)).list().future
}