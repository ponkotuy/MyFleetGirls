package honor

import models.db.Basic

/**
 *
 * @author ponkotuy
 * Date: 15/03/18.
 */
object Practice extends HonorCategory {
  override def category: Int = 7

  override def approved(memberId: Long): List[String] = {
    Basic.findByUser(memberId).map { basic =>
      if(basic.ptWin <= basic.ptLose) "負け組" :: Nil
      else if(basic.ptLose*4 <= basic.ptWin) "勝ち組" :: Nil
      else Nil
    }.getOrElse(Nil)
  }

  override val comment: String = "演習の勝率80%以上または50%以下"
}
