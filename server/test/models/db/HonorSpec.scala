package models.db

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import org.joda.time._
import scalikejdbc._

class HonorSpec extends Specification {

  "Honor" should {

    val h = Honor.syntax("h")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Honor.find(1L)
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Honor.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Honor.countAll()
      count should be_>(0L)
    }
    "find by where clauses" in new AutoRollback {
      val results = Honor.findAllBy(sqls.eq(h.id, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Honor.countBy(sqls.eq(h.id, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Honor.create(memberId = 1L, category = 123, name = "MyString", set = false)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Honor.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Honor.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Honor.findAll().head
      Honor.destroy(entity)
      val shouldBeNone = Honor.find(1L)
      shouldBeNone.isDefined should beFalse
    }
  }

}
        