import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._

/**
 *
 * @author ponkotuy
 * Date: 15/02/10.
 */
object Settings {
  private val Elems = Array("driver", "url", "user", "password").map("db.default." + _)
  val config = ConfigFactory.load()
  val dbMap: Map[String, String] = Elems.flatMap { elem =>
    config.getAs[String](elem).map { it => elem -> it }
  }.toMap
}
