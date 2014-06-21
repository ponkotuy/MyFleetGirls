package tool

/**
 * Date: 14/06/21.
 */
case class MatDiff(now: Int, diff: Int) {
  def cell = {
    if(diff < 0) <td>{now} <span class="text-danger">{diff}</span></td>
    else <td>{now} <span class="text-primary">+{diff}</span></td>
  }
}
