package de.admir.safetzec.rendering

import reactivemongo.api.MongoConnection
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.document

import scala.concurrent.{ExecutionContext, Future}


trait TemplateStore {
  def saveTemplate(name: String, value: String): Future[Unit]

  def findTemplate(name: String): Future[Option[String]]
}

class MongoTemplateStore(mongoConnection: MongoConnection)(implicit ec: ExecutionContext) extends TemplateStore {

  private val templateDbFut = mongoConnection.database("template")
  private val fmTmplCollFut: Future[BSONCollection] = templateDbFut.map(_.collection("freemarker"))

  def saveTemplate(name: String, value: String): Future[Unit] = {
    fmTmplCollFut.flatMap(_.insert(document("name" -> name, "value" -> value))).map(_ => ())
  }

  def findTemplate(name: String): Future[Option[String]] = {
    fmTmplCollFut.flatMap(_.find(document("name" -> name)).one).map(_.flatMap(_.getAs[String]("value")))
  }
}
