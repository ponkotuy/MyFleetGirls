package plugins

import play.api._
import scalikejdbc.config.{DBs, TypesafeConfigReader, TypesafeConfig}
import scalikejdbc.{JDBCSettings, ConnectionPool}

/**
 *
 * @author ponkotuy
 * Date: 14/05/01.
 */
class MyConnectionPool(implicit app: Application) extends Plugin {

  /**
   * DBs with Play application configuration.
   */
  private[this] lazy val DBs = new DBs with TypesafeConfigReader with TypesafeConfig {
    override val config = app.configuration.underlying
    implicit val factory = C3P0ConnectionPoolFactory

    // Change ConnectionPoolFactory
    override def setup(dbName: Symbol = ConnectionPool.DEFAULT_NAME): Unit = {
      val JDBCSettings(url, user, password, driver) = readJDBCSettings(dbName)
      val cpSettings = readConnectionPoolSettings(dbName)
      Class.forName(driver)
      ConnectionPool.add(dbName, url, user, password, cpSettings)
    }
  }

  override def onStart(): Unit = {
    DBs.setupAll()
  }

  override def onStop(): Unit = {
    ConnectionPool.closeAll()
  }

}
