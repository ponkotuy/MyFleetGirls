package models

import scalikejdbc._
import util.scalikejdbc.ExtendedQueryDSLFeature

/**
 *
 * @author ponkotuy
 * Date: 14/05/02.
 */
object SQLInterpolation
  extends SQLInterpolationFeature
  with SQLSyntaxSupportFeature
  with QueryDSLFeature
  with ExtendedQueryDSLFeature // Ponkotu拡張
