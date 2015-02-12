import controllers.ViewSta
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Await
import scala.util.Try
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 15/02/13.
 */
object DropStage extends App {
  import Common._
  val count = Try { args(0).toInt }.getOrElse(100)
  running(FakeApplication(additionalConfiguration = Settings.dbMap)) {
    val futures = (1 to count).map { _ =>
      ViewSta.dropStage().apply(FakeRequest())
    }
    futures.map { f =>
      Await.result(f, 10.second).body.run(printIteratee)
    }
  }
}
