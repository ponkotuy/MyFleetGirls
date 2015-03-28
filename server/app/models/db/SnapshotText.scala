package models.db

import scalikejdbc._

case class SnapshotText(
  id: Long,
  content: String) {

  def save()(implicit session: DBSession = SnapshotText.autoSession): SnapshotText = SnapshotText.save(this)(session)

  def destroy()(implicit session: DBSession = SnapshotText.autoSession): Unit = SnapshotText.destroy(this)(session)

}


object SnapshotText extends SQLSyntaxSupport[SnapshotText] {

  override val tableName = "snapshot_text"

  override val columns = Seq("id", "content")

  def apply(st: SyntaxProvider[SnapshotText])(rs: WrappedResultSet): SnapshotText = apply(st.resultName)(rs)
  def apply(st: ResultName[SnapshotText])(rs: WrappedResultSet): SnapshotText = new SnapshotText(
    id = rs.get(st.id),
    content = rs.get(st.content)
  )

  val st = SnapshotText.syntax("st")
  val msi = MasterSlotItem.syntax("msi")
  val ms = MasterShipBase.syntax("ms")
  val dss = DeckShipSnapshot.syntax("dss")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[SnapshotText] = {
    withSQL {
      select.from(SnapshotText as st).where.eq(st.id, id)
    }.map(SnapshotText(st.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[SnapshotText] = {
    withSQL(select.from(SnapshotText as st)).map(SnapshotText(st.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(SnapshotText as st)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[SnapshotText] = {
    withSQL {
      select.from(SnapshotText as st).where.append(sqls"${where}")
    }.map(SnapshotText(st.resultName)).list().apply()
  }

  def search(q: String, limit: Int = 10, offset: Int = 0)(implicit session: DBSession = autoSession): List[Long] = {
    withSQL {
      select(column.id).from(SnapshotText as st)
          .where(sqls"MATCH(content) AGAINST(${q})")
          .orderBy(sqls"MATCH(content) AGAINST(${q})").desc
          .limit(limit).offset(offset)
    }.map(_.long(1)).list().apply()
  }

  def searchCount(q: String)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(SnapshotText as st)
          .where(sqls"MATCH(content) AGAINST(${q})")
    }.map(_.long(1)).single().apply().getOrElse(0L)
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(SnapshotText as st).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(snap: DeckSnapshot)(implicit session: DBSession = autoSession): Unit = {
    val ships = DeckShipSnapshot.findAllBy(sqls.eq(dss.deckId, snap.id))
    val equips = ships.flatMap(_.slot)
    val equipNames = MasterSlotItem.findAllBy(sqls.in(msi.id, equips)).map(_.name)
    val shipNames = MasterShipBase.findAllBy(sqls.in(ms.id, ships.map(_.shipId))).map(_.name)
    val content = (Vector(snap.name, snap.title, snap.comment) ++ equipNames ++ shipNames).mkString(" ")
    createOrig(snap.id, content)
  }

  def createOrig(
    id: Long,
    content: String)(implicit session: DBSession = autoSession): SnapshotText = {
    withSQL {
      insert.into(SnapshotText).columns(
        column.id,
        column.content
      ).values(
            id,
            content
          )
    }.update().apply()

    SnapshotText(
      id = id,
      content = content)
  }

  def save(entity: SnapshotText)(implicit session: DBSession = autoSession): SnapshotText = {
    withSQL {
      update(SnapshotText).set(
        column.id -> entity.id,
        column.content -> entity.content
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: SnapshotText)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(SnapshotText).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
