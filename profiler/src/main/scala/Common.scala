import play.api.libs.iteratee.Iteratee
import play.api.libs.concurrent.Execution.Implicits._

/**
 *
 * @author ponkotuy
 * Date: 15/02/13.
 */
object Common {
  val printIteratee: Iteratee[Array[Byte], Unit] = Iteratee.foreach(ary => println(ary.length))
}
