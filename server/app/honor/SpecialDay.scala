package honor

import com.github.nscala_time.time.Imports._

/**
 *
 * @author ponkotuy
 * Date: 15/04/01.
 */
object SpecialDay extends HonorCategory {
  override def category: Int = 15

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val now = DateTime.now(DateTimeZone.forOffsetHours(9))
    val monthDay = MonthDay.fromJoda(now)
    Honors.get(monthDay).toList
  }

  val Honors = Map(
    MonthDay(1, 1) -> "正月",
    MonthDay(2, 14) -> "バレンタインデー",
    MonthDay(4, 1) -> "エイプリルフール",
    MonthDay(7, 7) -> "七夕",
    MonthDay(8, 15) -> "終戦記念日",
    MonthDay(10, 31) -> "ハロウィン",
    MonthDay(12, 25) -> "クリスマス"
  )

  override val comment: String = "固有の日に称号ページを開く"
}

case class MonthDay(month: Int, day: Int)

object MonthDay {
  def fromJoda(date: DateTime): MonthDay = new MonthDay(date.getMonthOfYear, date.getDayOfMonth)
}
