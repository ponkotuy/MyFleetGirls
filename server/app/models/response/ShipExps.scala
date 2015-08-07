package models.response

/**
 *
 * @author ponkotuy
 * Date: 15/05/27.
 */
case class ShipExps(shipId: Int, name: String, exps: List[Exp])

case class Exp(exp: Int, created: Long)
