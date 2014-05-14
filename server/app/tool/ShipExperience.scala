package tool

/**
 *
 * @author ponkotuy
 * Date: 14/04/14.
 */
object ShipExperience {
  val Upper92 = Map(
    92 -> 20000,
    93 -> 22000,
    94 -> 25000,
    95 -> 30000,
    96 -> 40000,
    97 -> 60000,
    98 -> 90000,
    99 -> 148500,
    100 -> 0,
    101 -> 10000
  )

  def diff(lv: Int): Int = {
    if(lv <= 51) (lv - 1)*100
    else if(lv <= 61) 5000 + (lv - 51)*200
    else if(lv <= 71) 7000 + (lv - 61)*300
    else if(lv <= 81) 10000 + (lv - 71)*400
    else if(lv <= 91) 14000 + (lv - 81)*500
    else if(lv <= 101) Upper92(lv)
    else if(lv <= 111) (lv - 101)*1000
    else if(lv <= 116) 10000 + (lv - 111)*2000
    else if(lv <= 121) 20000 + (lv - 116)*3000
    else if(lv <= 131) 35000 + (lv - 121)*4000
    else if(lv <= 140) 75000 + (lv - 131)*5000
    else if(lv <= 145) 120000 + (lv - 140)*7000
    else 155000 + (lv - 145)*8000
  }

  val _sum: Stream[(Int, Int)] = (1, 0) #:: _sum.map { case (lv, exp) => (lv + 1, exp + diff(lv + 1)) }

  def sum(lv: Int): Int = _sum(lv - 1)._2
}
