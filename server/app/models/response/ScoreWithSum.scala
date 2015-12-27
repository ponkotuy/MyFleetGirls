package models.response

case class ScoreWithSum(
    memberId: Long,
    score: Int,
    monthlyExp: Int,
    yearlyExp: Int,
    eo: Int,
    lastEo: Int,
    yyyymmmddhh: Int,
    created: Long)
