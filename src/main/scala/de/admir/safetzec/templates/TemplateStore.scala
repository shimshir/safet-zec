package de.admir.safetzec.templates

import java.util.concurrent.ConcurrentHashMap

import com.github.simplyscala.MongoEmbedDatabase
import de.admir.safetzec.models.TemplateModel
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.document

import scala.concurrent.{ExecutionContext, Future}


trait TemplateStore {
  def saveTemplate(templateModel: TemplateModel): Future[String]

  def findTemplate(name: String): Future[Option[TemplateModel]]
}

class InMemoryTemplateStore() extends TemplateStore {
  private val templateMap = new ConcurrentHashMap[String, TemplateModel]()

  def saveTemplate(templateModel: TemplateModel): Future[String] = {
    templateMap.put(templateModel.name, templateModel)
    Future.successful(templateModel.name)
  }

  def findTemplate(name: String): Future[Option[TemplateModel]] = Future.successful(Option(templateMap.get(name)))
}

class MongoTemplateStore(mongoConnection: MongoConnection)(implicit ec: ExecutionContext) extends TemplateStore {

  import de.admir.safetzec.models.MongoProtocols._

  private val templateDbFut = mongoConnection.database("safetzec")
  private val tmplCollFut: Future[BSONCollection] = templateDbFut.map(_.collection("templates"))

  // TODO: Handle updates with PUT requests in the route, not here
  def saveTemplate(templateModel: TemplateModel): Future[String] = {
    findTemplate(templateModel.name).flatMap {
      case Some(_) =>
        updateTemplate(templateModel)
      case None =>
        createTemplate(templateModel)
    }
  }

  private def updateTemplate(templateModel: TemplateModel): Future[String] = {
    tmplCollFut.flatMap(_.update(document("name" -> templateModel.name), templateModel).map(_ => templateModel.name))
  }

  private def createTemplate(templateModel: TemplateModel): Future[String] = {
    tmplCollFut.flatMap(_.insert(templateModel).map(_ => templateModel.name))
  }

  def findTemplate(name: String): Future[Option[TemplateModel]] = {
    tmplCollFut.flatMap(_.find(document("name" -> name)).one[TemplateModel])
  }
}

object DefaultMongoTemplateStore extends MongoEmbedDatabase {
  def createConnection(port: Int): MongoConnection = {
    mongoStart(port)
    val driver = MongoDriver()
    val parsedUri = MongoConnection.parseURI(s"mongodb://localhost:$port")
    parsedUri.map(driver.connection).get
  }
}
