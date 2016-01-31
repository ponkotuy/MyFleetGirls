package tool

import models.join.Stage


/**
  * @param score Clear score
  * @param clear Required clear count
  * @param boss Is exists boss
  */
case class StageInfo(stage: Stage, score: Int, clear: Int, boss: Boolean)

object StageInfo {
  val values = Vector(
    StageInfo(Stage(1, 5), 75, 4, boss = true),
    StageInfo(Stage(1, 6), 75, 7, boss = false),
    StageInfo(Stage(2, 5), 100, 4, boss = true),
    StageInfo(Stage(3, 5), 150, 4, boss = true),
    StageInfo(Stage(4, 5), 180, 5, boss = true),
    StageInfo(Stage(5, 5), 200, 5, boss = true)
  )
}
