package controllers

import models.Material
import com.github.nscala_time.time.Imports._

/**
 * Date: 14/06/21.
 */
case class MatDiff(now: Int, diff: Int) {
  def cell = {
    if(diff < 0) <td>{now} <span class="text-danger">{diff}</span></td>
    else <td>{now} <span class="text-primary">+{diff}</span></td>
  }
}

case class MaterialDays(
    day: LocalDate,
    fuel: MatDiff,
    ammo: MatDiff,
    steel: MatDiff,
    bauxite: MatDiff,
    instant: MatDiff,
    bucket: MatDiff,
    develop: MatDiff
) {
  def prettyDay: String = day.toString("M月d日")
}

object MaterialDays {
  def fromMaterials(day: LocalDate, x: Material, y: Material): MaterialDays = {
    MaterialDays(day,
      f(x.fuel, y.fuel),
      f(x.ammo, y.ammo),
      f(x.steel, y.steel),
      f(x.bauxite, y.bauxite),
      f(x.instant, y.instant),
      f(x.bucket, y.bucket),
      f(x.develop, y.develop)
    )
  }

  private def f(now: Int, prev: Int) = MatDiff(now, now - prev)
}
