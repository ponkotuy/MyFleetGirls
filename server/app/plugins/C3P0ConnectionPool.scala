package plugins

import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}
import javax.sql.DataSource
import java.sql.Connection
import com.mchange.v2.c3p0.ComboPooledDataSource

/**
  * c3p0 Connection Pool Customized
  */
class C3P0ConnectionPool(
     override val url: String,
     override val user: String,
     password: String,
     override val settings: ConnectionPoolSettings = ConnectionPoolSettings())
  extends ConnectionPool(url, user, password, settings) {

     private[this] val _dataSource = new ComboPooledDataSource
     _dataSource.setJdbcUrl(url)
     _dataSource.setUser(user)
     _dataSource.setPassword(password)
     _dataSource.setInitialPoolSize(settings.initialSize)
     _dataSource.setMaxPoolSize(settings.maxSize)
     _dataSource.setCheckoutTimeout(settings.connectionTimeoutMillis.toInt)

     // Customized
     _dataSource.setIdleConnectionTestPeriod(10)

     override def dataSource: DataSource = _dataSource
     override def borrow(): Connection = dataSource.getConnection
     override def numActive: Int = _dataSource.getNumBusyConnections(user, password)
     override def numIdle: Int = _dataSource.getNumIdleConnections(user, password)
     override def maxActive: Int = _dataSource.getMaxPoolSize
     override def maxIdle: Int = _dataSource.getMaxPoolSize
     override def close(): Unit = _dataSource.close()
}
