package util.scalikejdbc

import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/05/02.
 */
trait ExtendedQueryDSLFeature extends QueryDSLFeature { self: SQLInterpolationFeature with SQLSyntaxSupportFeature =>
  object insertIgnore {
    def into(support: SQLSyntaxSupport[_]): InsertSQLBuilder =
      new InsertSQLBuilder(sqls"insert ignore into ${support.table}")
  }

  object upsert {
    def into(support: SQLSyntaxSupport[_]): InsertSQLBuilder =
      new InsertSQLBuilder(sqls"insert on duplicate key update into ${support.table}")
  }
}
