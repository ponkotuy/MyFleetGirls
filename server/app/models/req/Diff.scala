package models.req

/**
 * Date: 14/06/21.
 */
case class Diff(now: Int, diff: Int) {
  def cell(cls: String = "") = {
    if(diff < 0) <td class={cls}>{now} <small class="text-danger">{diff}</small></td>
    else if(diff > 0) <td class={cls}>{now} <small class="text-primary">+{diff}</small></td>
    else <td class={cls}>{now}</td>
  }
}

object Diff {
  def diff(now: Int, prev: Int) = Diff(now, now - prev)
}
