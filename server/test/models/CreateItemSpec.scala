package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import org.joda.time._
import scalikejdbc.SQLInterpolation._

class CreateItemSpec extends Specification {
  val ci = CreateItem.syntax("ci")

  "CreateItem" should {
    "find by primary keys" in new AutoRollback {
      val maybeFound = CreateItem.find(1L)
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = CreateItem.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = CreateItem.countAll()
      count should be_>(0L)
    }
    "find by where clauses" in new AutoRollback {
      val results = CreateItem.findAllBy(sqls.eq(ci.id, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = CreateItem.countBy(sqls.eq(ci.id, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = CreateItem.create(memberId = 1L, fuel = 123, ammo = 123, steel = 123, bauxite = 123, createFlag = false, shizaiFlag = false, flagship = 123)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = CreateItem.findAll().head
      val updated = CreateItem.save(entity)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = CreateItem.findAll().head
      CreateItem.destroy(entity)
      val shouldBeNone = CreateItem.find(1L)
      shouldBeNone.isDefined should beFalse
    }
  }

}
        