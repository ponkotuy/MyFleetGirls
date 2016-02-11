package com.ponkotuy.value

/**
 *
 * @author ponkotuy
 * Date: 15/07/24.
 */
object ShipIds {
  val Akebono = 15
  val Mochizuki = 31
  val Hibiki = 35
  val Yudachi = 45
  val Tenryu = 51
  val Sendai = 54
  val Naka = 56
  val Chokai = 69
  val Tone = 71
  val Kirishima = 85
  val Sazanami = 94
  val Mikuma = 120
  val Kumano = 125
  val I8 = 128
  val Yamato = 131
  val Makigumo = 134
  val Noshiro = 138
  val Yahagi = 139
  val Sakawa = 140
  val IsuzuMk2 = 141
  val Musashi = 143
  val Vernyj = 147 // べーるぬい
  val Taiho = 153
  val Katori = 154
  val I401 = 155
  val AkitsuMaru = 161
  val Tanikaze = 169
  val Bismarck = 171
  val Ooyodo = 183
  val Taigei = 184
  val Ryuho = 185
  val Hatsukaze = 190
  val Akiduki = 330
  val Teruduki = 346
  val Unryu = 404
  val Harusame = 405
  val Hayashimo = 409
  val Kiyoshimo = 410
  val Asagumo = 413
  val Nowaki = 415
  val AkidukiMk1 = 421
  val TerudukiMk1 = 422
  val Asashimo = 425
  val MayaMk2 = 428
  val U511 = 431
  val Ro500 = 436
  val Littorio = 441
  val Roma = 442
  val Italia = 446
  val Okinami = 452
  val KasumiMk2Otsu = 470

  def isEnemy(id: Int): Boolean = 500 < id && id <= 900
}
