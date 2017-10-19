package de.admir.safetzec.templates

import akka.event.slf4j.SLF4JLogging
import com.github.simplyscala.MongoEmbedDatabase
import de.admir.safetzec.models.TemplateModel
import reactivemongo.api.{Cursor, MongoConnection, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.document

import scala.concurrent.{ExecutionContext, Future}

class MongoTemplateStore(mongoConnection: MongoConnection)(implicit ec: ExecutionContext) extends TemplateStore {

  import de.admir.safetzec.models.MongoProtocols._

  private val templateDbFut = mongoConnection.database("safetzec")
  private val tmplCollFut: Future[BSONCollection] = templateDbFut.map(_.collection("templates"))

  def templates: Future[Seq[TemplateModel]] = {
    tmplCollFut.flatMap(_.find(document()).cursor().collect(-1, Cursor.ContOnError[Seq[TemplateModel]]()))
  }

  def saveTemplate(templateModel: TemplateModel): Future[Throwable Either String] = {
    findTemplate(templateModel.name).flatMap {
      case Some(_) =>
        updateTemplate(templateModel)
      case None =>
        createTemplate(templateModel)
    }
  }

  private def updateTemplate(templateModel: TemplateModel) = {
    tmplCollFut
      .flatMap(_.update(document("name" -> templateModel.name), templateModel).map(_ => templateModel.name))
      .map(Right(_))
      .recover { case t => Left(t) }
  }

  private def createTemplate(templateModel: TemplateModel) = {
    tmplCollFut
      .flatMap(_.insert(templateModel).map(_ => templateModel.name))
      .map(Right(_))
      .recover { case t => Left(t) }
  }

  def findTemplate(name: String): Future[Option[TemplateModel]] = {
    tmplCollFut.flatMap(_.find(document("name" -> name)).one[TemplateModel])
  }
}

object MongoTemplateStore extends MongoEmbedDatabase with SLF4JLogging {
  def store(port: Int)(implicit ec: ExecutionContext): TemplateStore = {
    log.debug(s"Starting embedded mongo on port: $port")
    val mongoProps = mongoStart(port)
    val driver = MongoDriver()
    val parsedUri = MongoConnection.parseURI(s"mongodb://localhost:$port")
    val connection = parsedUri.map(driver.connection).get

    sys.addShutdownHook {
      log.debug("Stopping embedded mongo")
      mongoStop(mongoProps)
      log.debug("Embedded mongo stopped")
    }
    new MongoTemplateStore(connection)
  }
}