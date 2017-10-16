package de.admir.safetzec.rendering

import de.admir.safetzec.models.EngineEnum.EngineEnum
import de.admir.safetzec.models.{RenderRequest, TemplateData}
import de.admir.safetzec.templates.TemplateStore
import spray.json.JsObject

import scala.concurrent.{ExecutionContext, Future}

class RenderingService(supportedEngines: Map[EngineEnum, RenderingEngine], templateStore: TemplateStore)(implicit ec: ExecutionContext) {
  private def renderByName(data: JsObject, name: String): Future[Option[Throwable Either String]] = {
    templateStore.findTemplate(name).map(_.map(templateModel => renderByValue(data, templateModel.value, templateModel.engine)))
  }

  private def renderByValue(data: JsObject, value: String, engineEnum: EngineEnum): Throwable Either String = {
    supportedEngines.get(engineEnum).map(_.render(data, value, None)) getOrElse Left(new IllegalStateException(s"No RenderingEngine found for engineEnum: $engineEnum"))
  }

  def render(renderRequest: RenderRequest): Future[Option[Throwable Either String]] = {
    renderRequest.template match {
      case TemplateData(Some(name), None, _) =>
        renderByName(renderRequest.data, name)
      case TemplateData(None, Some(value), Some(engine)) =>
        Future.successful(Some(renderByValue(renderRequest.data, value, engine)))
      case td =>
        Future.successful(Some(Left(new IllegalArgumentException(s"Illegal template data: $td"))))
    }
  }
}
