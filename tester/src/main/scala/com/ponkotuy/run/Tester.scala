package com.ponkotuy.run

import java.io.{BufferedReader, InputStreamReader}

import pl.project13.scala.rainbow._
import skinny.http.HTTP

import scala.util.{Failure, Success, Try}

/**
 *
 * @author ponkotuy
 * Date: 15/03/03.
 */
object Tester extends App {
  val Host = args.headOption.getOrElse("http://localhost:9000")
  val urlFile = getClass.getResource("/urls")
  val br = new BufferedReader(new InputStreamReader(urlFile.openStream()))
  Iterator.continually(br.readLine()).takeWhile(_ != null).foreach { line =>
    val url = Host + line
    Try {
      HTTP.get(url).status
    } match {
      case Success(200) => println(s"Success: $url".green)
      case Success(status) if 300 <= status && status < 400 => println(s"Redirect ${status}: $url".green)
      case Success(status) => println(s"Bad status $status: $url".red)
      case Failure(e) => println(s"Error ${e.getMessage}: $url".red)
    }
  }
}
