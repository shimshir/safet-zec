package de.admir.safetzec.templates

import java.util.concurrent.ConcurrentHashMap

import de.admir.safetzec.models.TemplateModel
import scala.concurrent.Future

class InMemoryTemplateStore() extends TemplateStore {
  private val templateMap = new ConcurrentHashMap[String, TemplateModel]()

  import scala.collection.JavaConverters._

  def templates: Future[Seq[TemplateModel]] = Future.successful(templateMap.values().asScala.toSeq)

  def saveTemplate(templateModel: TemplateModel): Future[Throwable Either String] = {
    templateMap.put(templateModel.name, templateModel)
    Future.successful(Right(templateModel.name))
  }

  def findTemplate(name: String): Future[Option[TemplateModel]] = Future.successful(Option(templateMap.get(name)))
}


