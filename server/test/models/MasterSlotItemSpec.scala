package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import org.joda.time._
import scalikejdbc.SQLInterpolation._

class MasterSlotItemSpec extends Specification {
  val msi = MasterSlotItem.syntax("msi")

  "MasterSlotItem" should {
    "find by primary keys" in new AutoRollback {
      val maybeFound = MasterSlotItem.find(123)
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = MasterSlotItem.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = MasterSlotItem.countAll()
      count should be_>(0L)
    }
    "find by where clauses" in new AutoRollback {
      val results = MasterSlotItem.findAllBy(sqls.eq(msi.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = MasterSlotItem.countBy(sqls.eq(msi.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = MasterSlotItem.create(id = 123, name = "MyString", typ = "MyString", power = 123, torpedo = 123, bomb = 123, antiair = 123, antisub = 123, search = 123, hit = 123, length = 123, rare = 123, info = "MyString")
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = MasterSlotItem.findAll().head
      val updated = MasterSlotItem.save(entity)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = MasterSlotItem.findAll().head
      MasterSlotItem.destroy(entity)
      val shouldBeNone = MasterSlotItem.find(123)
      shouldBeNone.isDefined should beFalse
    }
  }

}
        