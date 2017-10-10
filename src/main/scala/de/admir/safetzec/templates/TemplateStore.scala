package de.admir.safetzec.templates

import java.util.concurrent.ConcurrentHashMap

import akka.event.slf4j.SLF4JLogging
import com.github.simplyscala.MongoEmbedDatabase
import de.admir.safetzec.models.{EngineEnum, TemplateModel}
import org.kohsuke.github.GitHub
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.document

import scala.concurrent.{ExecutionContext, Future}


trait TemplateStore {
  def saveTemplate(templateModel: TemplateModel): Future[Throwable Either String]

  def findTemplate(name: String): Future[Option[TemplateModel]]
}

class InMemoryTemplateStore() extends TemplateStore {
  private val templateMap = new ConcurrentHashMap[String, TemplateModel]()

  def saveTemplate(templateModel: TemplateModel): Future[Throwable Either String] = {
    templateMap.put(templateModel.name, templateModel)
    Future.successful(Right(templateModel.name))
  }

  def findTemplate(name: String): Future[Option[TemplateModel]] = Future.successful(Option(templateMap.get(name)))
}

class MongoTemplateStore(mongoConnection: MongoConnection)(implicit ec: ExecutionContext) extends TemplateStore {

  import de.admir.safetzec.models.MongoProtocols._

  private val templateDbFut = mongoConnection.database("safetzec")
  private val tmplCollFut: Future[BSONCollection] = templateDbFut.map(_.collection("templates"))

  def this() {
    this(DefaultMongoTemplateStore.createConnection(12345))
  }

  // TODO: Handle updates with PUT requests in the route, not here
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

object DefaultMongoTemplateStore extends MongoEmbedDatabase with SLF4JLogging {
  def createConnection(port: Int): MongoConnection = {
    val mongoProps = mongoStart(port)
    sys.addShutdownHook {
      log.debug("Stopping mongo")
      mongoStop(mongoProps)
      log.debug("Mongo stopped")
    }

    val driver = MongoDriver()
    val parsedUri = MongoConnection.parseURI(s"mongodb://localhost:$port")
    parsedUri.map(driver.connection).get
  }
}

class GithubTemplateStore(github: GitHub, repoName: String, templateFolder: String)(implicit ec: ExecutionContext) extends TemplateStore with SLF4JLogging {
  private val repo = github.getRepository(repoName)

  def saveTemplate(templateModel: TemplateModel): Future[Throwable Either String] = {
    val fileExtension = extractExtension(templateModel.name)
    extensionToEngineEnum(fileExtension).map { _ =>
      findTemplate(templateModel.name).flatMap {
        case Some(_) => updateTemplate(templateModel)
        case None => createTemplate(templateModel)
      }
    } getOrElse Future.successful(Left(new IllegalArgumentException(s"Unsupported file extension: $fileExtension")))
  }

  private def createTemplate(templateModel: TemplateModel): Future[Throwable Either String] = Future {
    repo.createContent(
      templateModel.value,
      s"Created template",
      s"$templateFolder/${templateModel.name}",
      "master"
    )
    templateModel.name
  } map (Right(_)) recover { case t => Left(t) }


  private def updateTemplate(templateModel: TemplateModel): Future[Throwable Either String] = Future {
    repo.getFileContent(s"$templateFolder/${templateModel.name}").update(templateModel.value, "Updated template")
    templateModel.name
  } map (Right(_)) recover { case t => Left(t) }


  def findTemplate(name: String): Future[Option[TemplateModel]] = Future {
    github.refreshCache()
    val fileContent = repo.getFileContent(s"$templateFolder/$name")
    val inputStream = fileContent.read()
    val templateValue = io.Source.fromInputStream(inputStream).mkString
    val engineOpt = extensionToEngineEnum(extractExtension(name))
    engineOpt.map(TemplateModel(name, templateValue, _))
  } recover { case _ => None }

  private def extractExtension(name: String) = name.reverse.takeWhile(_ != '.').reverse

  private def extensionToEngineEnum(extension: String) = {
    extension.toLowerCase match {
      case "dust" => Some(EngineEnum.DUST)
      case "ftl" => Some(EngineEnum.FREEMARKER)
      case "hbs" => Some(EngineEnum.HANDLEBARS)
      case unknownExtension =>
        log.warn(s"Unknown template extension: $unknownExtension")
        None
    }
  }
}
