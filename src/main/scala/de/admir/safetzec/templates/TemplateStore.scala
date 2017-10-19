package de.admir.safetzec.templates

import de.admir.safetzec.models.TemplateModel
import scala.concurrent.Future


trait TemplateStore {
  def templates: Future[Seq[TemplateModel]]

  def saveTemplate(templateModel: TemplateModel): Future[Throwable Either String]

  def findTemplate(name: String): Future[Option[TemplateModel]]
}

