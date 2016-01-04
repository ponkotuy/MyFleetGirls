package models.db

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class ItemSnapshotSpec extends Specification {

  "ItemSnapshot" should {

    val is = ItemSnapshot.syntax("is")

    "find by primary keys" in new AutoRollback {
      val maybeFound = ItemSnapshot.find(1L)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = ItemSnapshot.findBy(sqls.eq(is.id, 1L))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = ItemSnapshot.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = ItemSnapshot.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = ItemSnapshot.findAllBy(sqls.eq(is.id, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = ItemSnapshot.countBy(sqls.eq(is.id, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = ItemSnapshot.create(memberId = 1L, shipSnapshotId = 1L, position = 1, slotitemId = 123, level = 123, created = 1L)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = ItemSnapshot.findAll().head
      // TODO modify something
      val modified = entity
      val updated = ItemSnapshot.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = ItemSnapshot.findAll().head
      ItemSnapshot.destroy(entity)
      val shouldBeNone = ItemSnapshot.find(1L)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = ItemSnapshot.findAll()
      entities.foreach(e => ItemSnapshot.destroy(e))
      val batchInserted = ItemSnapshot.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
