import scala.util.Random

/**
 *
 * @author ponkotuy
 * Date: 15/02/14.
 */
object ListVector {
  def main(args: Array[String]) {
    val size = 1000
    val count = 10000
    val random = new Random(0)
    val list = (1 to size).map(_ => random.nextInt()).toList
    val vector = list.toVector
    val array = list.toArray
    val times = Map(
      "Vector Sort" -> timer { (1 to count).foreach { _ => vector.sorted.apply(10) } },
      "Array Sort" -> timer { (1 to count).foreach { _ => array.sorted.apply(10) } },
      "List Sort" -> timer { (1 to count).foreach { _ => list.sorted.apply(10) } }
    )
    println(times)
  }

  def timer(f: => Unit): Long = {
    val start = System.currentTimeMillis()
    f
    System.currentTimeMillis() - start
  }
}
