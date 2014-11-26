package models.view

import models.join.ItemMat
import models.query.Period

/**
 *
 * @author ponkotuy
 * Date: 14/11/27.
 */
case class CItem(mat: ItemMat, fromTo: Period) {
  def sType = mat.sTypeName
  def fuel = mat.fuel
  def ammo = mat.ammo
  def steel = mat.steel
  def bauxite = mat.bauxite
  def from = fromTo.fromStr
  def to = fromTo.toStr

  def title = s"${sType}/$fuel/$ammo/$steel/$bauxite"
}
