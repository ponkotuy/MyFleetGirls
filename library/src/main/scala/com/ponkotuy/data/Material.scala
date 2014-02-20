package com.ponkotuy.data

import scala.concurrent.Future
import scalikejdbc.async._
import scalikejdbc.SQLInterpolation._

/**
 *
 * @param instant : Instant Construction
 * @param develop : Development Material
 * @author ponkotuy
 * Date: 14/02/19.
 */
case class Material(
    fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    instant: Int, bucket: Int, develop: Int) extends ShortenedNames {
  def save()(implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Material] =
    Material.save(this)
}

object Material extends SQLSyntaxSupport[Material] with ShortenedNames {
  def fromSeq(s: Seq[Int]): Material = {
    Material(s(0), s(1), s(2), s(3), s(4), s(5), s(6))
  }

  def save(m: Material)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Material] = withSQL {
    update(Material).set(
      column.fuel -> m.fuel, column.ammo -> m.ammo, column.steel -> m.steel, column.bauxite -> m.bauxite,
      column.instant -> m.instant, column.bucket -> m.bucket, column.develop -> m.develop
    )
  }.update().future.map(_ => m)
}
