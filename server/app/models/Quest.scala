package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import util.scalikejdbc.BulkInsert._

case class Quest(
  memberId: Long,
  id: Int,
  category: Int,
  typ: Int,
  state: Int,
  title: String,
  detail: String,
  fuel: Int,
  ammo: Int,
  steel: Int,
  bauxite: Int,
  bonus: Boolean,
  progressFlag: Int,
  created: Long) {

  def save()(implicit session: DBSession = Quest.autoSession): Quest = Quest.save(this)(session)

  def destroy()(implicit session: DBSession = Quest.autoSession): Unit = Quest.destroy(this)(session)

}


object Quest extends SQLSyntaxSupport[Quest] {

  override val tableName = "quest"

  override val columns = Seq("member_id", "id", "category", "typ", "state", "title", "detail", "fuel", "ammo", "steel", "bauxite", "bonus", "progress_flag", "created")

  def apply(q: SyntaxProvider[Quest])(rs: WrappedResultSet): Quest = apply(q.resultName)(rs)
  def apply(q: ResultName[Quest])(rs: WrappedResultSet): Quest = new Quest(
    memberId = rs.get(q.memberId),
    id = rs.get(q.id),
    category = rs.get(q.category),
    typ = rs.get(q.typ),
    state = rs.get(q.state),
    title = rs.get(q.title),
    detail = rs.get(q.detail),
    fuel = rs.get(q.fuel),
    ammo = rs.get(q.ammo),
    steel = rs.get(q.steel),
    bauxite = rs.get(q.bauxite),
    bonus = rs.get(q.bonus),
    progressFlag = rs.get(q.progressFlag),
    created = rs.get(q.created)
  )

  val q = Quest.syntax("q")

  override val autoSession = AutoSession

  def find(id: Int, memberId: Long)(implicit session: DBSession = autoSession): Option[Quest] = {
    withSQL {
      select.from(Quest as q).where.eq(q.id, id).and.eq(q.memberId, memberId)
    }.map(Quest(q.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Quest] = {
    withSQL(select.from(Quest as q)).map(Quest(q.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Quest as q)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Quest] = {
    withSQL {
      select.from(Quest as q).where.append(sqls"${where}").orderBy(q.state, q.id)
    }.map(Quest(q.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Quest as q).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def createOrig(
    memberId: Long,
    id: Int,
    category: Int,
    typ: Int,
    state: Int,
    title: String,
    detail: String,
    fuel: Int,
    ammo: Int,
    steel: Int,
    bauxite: Int,
    bonus: Boolean,
    progressFlag: Int,
    created: Long)(implicit session: DBSession = autoSession): Quest = {
    withSQL {
      insert.into(Quest).columns(
        column.memberId,
        column.id,
        column.category,
        column.typ,
        column.state,
        column.title,
        column.detail,
        column.fuel,
        column.ammo,
        column.steel,
        column.bauxite,
        column.bonus,
        column.progressFlag,
        column.created
      ).values(
          memberId,
          id,
          category,
          typ,
          state,
          title,
          detail,
          fuel,
          ammo,
          steel,
          bauxite,
          bonus,
          progressFlag,
          created
        )
    }.update().apply()

    Quest(
      memberId = memberId,
      id = id,
      category = category,
      typ = typ,
      state = state,
      title = title,
      detail = detail,
      fuel = fuel,
      ammo = ammo,
      steel = steel,
      bauxite = bauxite,
      bonus = bonus,
      progressFlag = progressFlag,
      created = created)
  }

  def bulkInsert(xs: Seq[data.Quest], memberId: Long)(implicit session: DBSession = autoSession): Unit = {
    val current = System.currentTimeMillis()
    applyUpdate {
      insert.into(Quest)
        .columns(
          column.memberId, column.id,
          column.category, column.typ, column.state, column.title, column.detail,
          column.fuel, column.ammo, column.steel, column.bauxite,
          column.bonus, column.progressFlag, column.created
        ).multiValues(
          Seq.fill(xs.size)(memberId), xs.map(_.no),
          xs.map(_.category), xs.map(_.typ), xs.map(_.state), xs.map(_.title), xs.map(_.detail),
          xs.map(_.material.fuel), xs.map(_.material.ammo), xs.map(_.material.steel), xs.map(_.material.bauxite),
          xs.map(_.bonus), xs.map(_.progressFlag), Seq.fill(xs.size)(current)
        )
    }
  }

  def bulkUpsert(xs: Seq[data.Quest], memberId: Long)(implicit session: DBSession = autoSession): Unit = {
    val current = System.currentTimeMillis()
    val params = xs.map { q =>
      val xs = Seq(
        memberId, q.no, q.category, q.typ, q.state, q.title, q.detail,
        q.material.fuel, q.material.ammo, q.material.steel, q.material.bauxite,
        q.bonus, q.progressFlag, current
      ).map(x => sqls"$x")
      sqls"(${sqls.csv(xs:_*)})"
    }
    sql"replace quest values ${sqls.csv(params:_*)}".execute().apply()
  }

  def save(entity: Quest)(implicit session: DBSession = autoSession): Quest = {
    withSQL {
      update(Quest).set(
        column.memberId -> entity.memberId,
        column.id -> entity.id,
        column.category -> entity.category,
        column.typ -> entity.typ,
        column.state -> entity.state,
        column.title -> entity.title,
        column.detail -> entity.detail,
        column.fuel -> entity.fuel,
        column.ammo -> entity.ammo,
        column.steel -> entity.steel,
        column.bauxite -> entity.bauxite,
        column.bonus -> entity.bonus,
        column.progressFlag -> entity.progressFlag,
        column.created -> entity.created
      ).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId)
    }.update().apply()
    entity
  }

  def destroy(entity: Quest)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Quest).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId)
    }.update().apply()
  }

  def deleteAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(Quest).where.append(where)
  }

}
