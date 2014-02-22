package models

import scala.concurrent.Future
import scalikejdbc.async._
import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data

/**
 *
 * @param instant : Instant Construction
 * @param develop : Development Material
 * @author ponkotuy
 * Date: 14/02/19.
 */
case class Material(id: Long,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    instant: Int, bucket: Int, develop: Int,
    created: Long) extends ShortenedNames {
  def save()(implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Material] =
    Material.save(this)
}

object Material extends SQLSyntaxSupport[Material] with ShortenedNames {
  def save(m: Material)(
    implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Material] = withSQL {
    update(Material).set(
      column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
      column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop
    )
  }.update().future.map(_ => m)

  def create(m: data.Material)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Material] = {
    val created = System.currentTimeMillis()
    val idFuture = withSQL {
      insert.into(Material).namedValues(
        column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
        column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop,
        column.created -> created
      )
    }.updateAndReturnGeneratedKey().future()
    idFuture.map { id =>
      Material(id, m.fuel, m.ammo, m.steel, m.bauxite, m.instant, m.bucket, m.develop, created)
    }
  }
}
