package models.view

import models.join.Mat
import models.query.Period

/**
 *
 * @author ponkotuy
 * Date: 14/11/27.
 */
case class CShip(mat: Mat, fromTo: Period) {
  def fuel = mat.fuel
  def ammo = mat.ammo
  def steel = mat.steel
  def bauxite = mat.bauxite
  def develop = mat.develop
  def from = fromTo.fromStr
  def to = fromTo.toStr

  def title = s"$fuel/$ammo/$steel/$bauxite/$develop"
}
