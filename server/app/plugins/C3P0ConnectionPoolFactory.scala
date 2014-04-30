package plugins

import scalikejdbc.{ConnectionPoolSettings, ConnectionPoolFactory}

/**
 *
 * @author ponkotuy
 * Date: 14/05/01.
 */
object C3P0ConnectionPoolFactory extends ConnectionPoolFactory {
  override def apply(url: String, user: String, password: String, settings: ConnectionPoolSettings = ConnectionPoolSettings()) = {
    new C3P0ConnectionPool(url, user, password, settings)
  }
}
