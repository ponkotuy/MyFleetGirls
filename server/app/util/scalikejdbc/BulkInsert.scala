package util.scalikejdbc

import scalikejdbc.SQLInterpolation._

object BulkInsert {
  implicit class BulkInsertSQLBuilder(val self: InsertSQLBuilder) extends AnyVal {
    def multiValues(values: Seq[Any]*): InsertSQLBuilder = {
      val elems = values.transpose.map { xs =>
        val ys = xs.map(x => sqls"$x")
        sqls"(${sqls.csv(ys: _*)})"
      }
      self.append(sqls"values ${sqls.csv(elems: _*)}")
    }
  }
}
