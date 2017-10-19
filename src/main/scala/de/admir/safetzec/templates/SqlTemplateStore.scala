package de.admir.safetzec.templates


import de.admir.safetzec.models.TemplateModel
import scalikejdbc.DBSession

import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc._

import scala.util.Try

class SqlTemplateStore()(implicit dbSession: DBSession, ec: ExecutionContext) extends TemplateStore {

  private val tm = TemplateModel.syntax("tm")
  private val tmColumn = TemplateModel.column

  def templates: Future[Seq[TemplateModel]] = Future {
    withSQL(select.from(TemplateModel as tm))
      .map(TemplateModel(tm)).list.apply()
  }

  def saveTemplate(templateModel: TemplateModel): Future[Throwable Either String] = Future {
    Try {
      withSQL {
        insert.into(TemplateModel)
          .namedValues(
            tmColumn.name -> templateModel.name,
            tmColumn.value -> templateModel.value,
            tmColumn.engine -> templateModel.engine.toString
          )
      }.update().apply()
    }.toEither.map(_ => templateModel.name)
  }

  def findTemplate(name: String): Future[Option[TemplateModel]] = Future {
    withSQL {
      select.from(TemplateModel as tm)
        .where
        .eq(tm.name, name)
    }.map(TemplateModel(tm)).single.apply()
  }
}

object H2SqlTemplateStore {

  private lazy val dbSession: DBSession = {
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:safet", "sa", "")
    val session = AutoSession

    SQL(
      """
        |create table template_model (
        |  name varchar(256) unique not null,
        |  value text not null,
        |  engine varchar(30) not null
        |)
      """.stripMargin).execute.apply()(session)
    session
  }

  def store(implicit ec: ExecutionContext): TemplateStore = {
    new SqlTemplateStore()(dbSession, ec)
  }
}

object PostgreSqlTemplateStore {
  Class.forName("org.postgresql.Driver")

  private lazy val localDbSession: DBSession = {
    ConnectionPool.singleton("jdbc:postgresql://localhost:5432/safet", "amemic", "amemic")
    AutoSession
  }

  private lazy val herokuDbSession: DBSession = {
    val jdbcUrl = sys.env("JDBC_DATABASE_URL")
    val jdbcUser = sys.env("JDBC_DATABASE_USERNAME")
    val jdbcPass = sys.env("JDBC_DATABASE_PASSWORD")
    ConnectionPool.singleton(jdbcUrl, jdbcUser, jdbcPass)
    AutoSession
  }

  def localStore(implicit ec: ExecutionContext): TemplateStore = {
    new SqlTemplateStore()(localDbSession, ec)
  }

  def herokuStore(implicit ec: ExecutionContext): TemplateStore = {
    new SqlTemplateStore()(herokuDbSession, ec)
  }
}