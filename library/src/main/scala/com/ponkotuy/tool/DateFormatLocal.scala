package com.ponkotuy.tool

import java.text.SimpleDateFormat
import java.util.Date

/**
 *
 * @author ponkotuy
 * Date: 14/04/22.
 */
class DateFormatLocal(format: String = "yyyy/MM/dd HH:mm") extends ThreadLocal[SimpleDateFormat] {
  override protected def initialValue(): SimpleDateFormat = {
    new SimpleDateFormat(format)
  }

  def parse(source: String): Date = get().parse(source)
  def format(date: Date): String = get().format(date)
}

object DateFormatLocal {
  lazy val default = new DateFormatLocal()
}
