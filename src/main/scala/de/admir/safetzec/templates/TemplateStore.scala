package de.admir.safetzec.templates

import java.util.concurrent.ConcurrentHashMap

import com.github.simplyscala.MongoEmbedDatabase
import de.admir.safetzec.models.{EngineEnum, TemplateModel}
import org.kohsuke.github.GitHub
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.document

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try


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

object DefaultMongoTemplateStore extends MongoEmbedDatabase {
  def createConnection(port: Int): MongoConnection = {
    mongoStart(port)
    val driver = MongoDriver()
    val parsedUri = MongoConnection.parseURI(s"mongodb://localhost:$port")
    parsedUri.map(driver.connection).get
  }
}

class GithubTemplateStore(github: GitHub, repoName: String, templateFolder: String)(implicit ec: ExecutionContext) extends TemplateStore {
  private val repo = github.getRepository(repoName)

  def saveTemplate(templateModel: TemplateModel): Future[Throwable Either String] = {
    val fileExtension = templateModel.name.reverse.takeWhile(_ != '.').reverse
    extensionToEngineEnum(fileExtension).map { _ =>
      findTemplate(templateModel.name).flatMap {
        case Some(_) => updateTemplate(templateModel)
        case None => createTemplate(templateModel)
      }
    } getOrElse Future.successful(Left(new IllegalArgumentException(s"Unsupported file extension: $fileExtension")))
  }

  private def createTemplate(templateModel: TemplateModel): Future[Throwable Either String] = Future {
    Try {
      repo.createContent(
        templateModel.value,
        s"Created template",
        s"$templateFolder/${templateModel.name}",
        "master"
      )
      templateModel.name
    } toEither
  }

  private def updateTemplate(templateModel: TemplateModel): Future[Throwable Either String] = Future {
    Try {
      repo.getFileContent(s"$templateFolder/${templateModel.name}").update(templateModel.value, "Updated template")
      templateModel.name
    } toEither
  }

  def findTemplate(name: String): Future[Option[TemplateModel]] = Future {
    github.refreshCache()
    Try(repo.getFileContent(s"$templateFolder/$name")).toOption.flatMap { fileContent =>
      val inputStream = fileContent.read()
      val templateValue = io.Source.fromInputStream(inputStream).mkString
      val engineOpt = extensionToEngineEnum(name.reverse.takeWhile(_ != '.').reverse)
      engineOpt.map(TemplateModel(name, templateValue, _))
    }
  }

  private def extensionToEngineEnum(extension: String) = {
    extension.toLowerCase match {
      case "dust" => Some(EngineEnum.DUST)
      case "ftl" => Some(EngineEnum.FREEMARKER)
      case "hbs" => Some(EngineEnum.HANDLEBARS)
      case unknownExtension =>
        // log.warn(s"Unknown template extension: $unknownExtension")
        None
    }
  }
}
