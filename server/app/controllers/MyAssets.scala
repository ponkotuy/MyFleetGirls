package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * @author ponkotuy
 * Date: 15/03/10.
 */
class MyAssets extends Controller {
  def at(path: String, file: String, aggressiveCaching: Boolean = false) = Action.async { implicit req =>
    val accepts = req.headers.get(ACCEPT_ENCODING).map(_.split(",").map(_.stripMargin)).getOrElse(Array())
    if(accepts.contains("pack200-gzip")) {
      Assets.at(path, file + ".pack.gz").apply(req).flatMap { result =>
        if(result.header.status >= 400) Assets.at(path, file, aggressiveCaching).apply(req)
        else Future(result.withHeaders(CONTENT_ENCODING -> "pack200-gzip"))
      }
    } else {
      controllers.Assets.at(path, file, aggressiveCaching).apply(req)
    }
  }
}
