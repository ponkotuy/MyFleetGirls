package com.ponkotuy.tool

import java.nio.charset.Charset
import java.util.zip.Adler32

/**
 * Created by yosuke on 15/01/23.
 */
object Checksum {
  val DefaultCharset = Charset.forName("UTF-8")
  def fromSeq(xs: Seq[Any]): Long = {
    val adler32 = new Adler32()
    xs.foreach { x =>
      adler32.update(x.toString.getBytes(DefaultCharset))
    }
    adler32.getValue
  }
}
