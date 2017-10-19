package de.admir.safetzec.templates

import akka.event.slf4j.SLF4JLogging
import de.admir.safetzec.models.{EngineEnum, TemplateModel}
import org.kohsuke.github.GitHub

import scala.concurrent.{ExecutionContext, Future}

class GithubTemplateStore(github: GitHub, repoName: String, templateFolder: String)(implicit ec: ExecutionContext) extends TemplateStore with SLF4JLogging {
  private val repo = github.getRepository(repoName)

  def templates: Future[Seq[TemplateModel]] = ???

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
