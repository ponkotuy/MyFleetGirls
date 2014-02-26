package controllers

import play.api.mvc._
import com.ponkotuy.data.{Ship, MasterShip, Basic, Material}
import Common._

/**
 *
 * @author ponkotuy
 * Date: 14/02/21.
 */
object Post extends Controller {
  def basic = Action.async { request =>
    authentication(request) { auth =>
      withData[Basic](request) { basic =>
        models.Basic.create(basic, auth.id)
      }
    }
  }

  def material = Action.async { request =>
    authentication(request) { auth =>
      withData[Material](request) { material =>
        models.Material.create(material, auth.id)
      }
    }
  }

  def ship = Action.async { request =>
    authentication(request) { auth =>
      withData[List[Ship]](request) { ships =>
        models.Ship.deleteAllByUser(auth.id)
        ships.foreach(ship => models.Ship.create(ship, auth.id))
      }
    }
  }
}
