import scala.util.Try
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.test.FakeRequest
import play.api._
import scalikejdbc._
import UserShip.UserIterator

/**
 *
 * @author ponkotuy
 * Date: 14/04/16.
 */
object UserShipDB {
  def main(args: Array[String]): Unit = {
    Class.forName("com.mysql.jdbc.Driver")
    val settings = ConnectionPoolSettings(initialSize = 3, maxSize = 10)
    ConnectionPool.singleton("jdbc:mysql://localhost:3306/myfleet", "user", "password", settings)

    val count = Try { args(0).toInt }.getOrElse(20)
    val users = new UserIterator(0)
    DB readOnly { implicit session =>
      users.take(count).foreach { user =>
        val result = controllers.UserView.ship(user).apply(FakeRequest())
        println(Await.result(result, 60.seconds))
      }
    }
  }
}
