package controllers

import play.api.mvc._
import com.ponkotuy.data.{Ship, Basic, Material}
import Common._

/**
 *
 * @author ponkotuy
 * Date: 14/02/21.
 */
object Post extends Controller {
  def basic = authAndParse[Basic] { case (auth, basic) =>
    val isChange = models.Basic.findByUser(auth.id).exists(_.diff(basic) > 0.1)
    if(isChange) {
      models.Basic.create(basic, auth.id)
      Ok("Success")
    } else {
      Ok("No Change")
    }
  }

  def material = authAndParse[Material] { case (auth, material) =>
    val isChange = models.Material.findByUser(auth.id).exists(_.diff(material) > 0.01)
    if(isChange) {
      models.Material.create(material, auth.id)
      Ok("Success")
    } else {
      Ok("No Change")
    }
  }

  def ship = authAndParse[List[Ship]] { case (auth, ships) =>
    models.Ship.deleteAllByUser(auth.id)
    ships.foreach(ship => models.Ship.create(ship, auth.id))
    Ok("Success")
  }
}
