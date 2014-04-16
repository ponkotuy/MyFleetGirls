import dispatch._
import scala.util.{Random, Try}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 *
 * @author ponkotuy
 * Date: 14/04/16.
 */
object UserShip {
  val users = Vector(
    50533L, 56609L, 64834L, 67526L, 79915L, 102362L, 108979L, 137121L, 205461L, 221134L, 236256L, 602664L, 628349L,
    653192L, 712532L, 732798L, 786280L, 866832L, 10007732L, 10011251L, 12001801L, 14036255L, 14063223L
  )

  class UserIterator(seed: Int) extends Iterator[Long]{
    val random = new Random(seed)
    override def next(): Long = users(random.nextInt(users.size))
    override def hasNext: Boolean = true
  }

  def main(args: Array[String]): Unit = {
    val count = Try { args(0).toInt }.toOption.getOrElse(100)
    val itr = new UserIterator(0)
    val http = new Http
    val results = itr.take(count).map { user =>
      val req = :/("localhost", 9000) / "user" / user / "ship"
      http(req OK as.String)
    }
    results.foreach(it => println(it()))
  }
}
