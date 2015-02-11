import controllers.UserView
import play.api.libs.iteratee.Iteratee
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 15/02/10.
 */
object UserShipPlay extends App {
  val count = Try { args(0).toInt }.getOrElse(100)
  val itr = new UserIterator(0)
  val printIteratee: Iteratee[Array[Byte], Unit] = Iteratee.foreach(ary => println(ary.length))
  running(FakeApplication(additionalConfiguration = Settings.dbMap)) {
    val futures = itr.take(count).toList.map { user =>
      UserView.ship(user).apply(FakeRequest())
    }
    futures.map { f =>
      Await.result(f, 10.second).body.run(printIteratee)
    }
  }
}
