package models.req

import com.github.nscala_time.time.Imports._
import models.db.Material

/**
 * Date: 14/06/21.
 */
case class MaterialDays(
    day: LocalDate,
    fuel: Diff,
    ammo: Diff,
    steel: Diff,
    bauxite: Diff,
    instant: Diff,
    bucket: Diff,
    develop: Diff,
    revamping: Diff) {
  def prettyDay: String = day.toString("M月d日")
}

object MaterialDays {
  import Diff._
  def fromMaterials(day: LocalDate, x: Material, y: Material): MaterialDays = {
    MaterialDays(day,
      diff(x.fuel, y.fuel),
      diff(x.ammo, y.ammo),
      diff(x.steel, y.steel),
      diff(x.bauxite, y.bauxite),
      diff(x.instant, y.instant),
      diff(x.bucket, y.bucket),
      diff(x.develop, y.develop),
      diff(x.revamping, y.revamping)
    )
  }
}
