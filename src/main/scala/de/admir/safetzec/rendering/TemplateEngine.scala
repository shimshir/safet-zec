package de.admir.safetzec.rendering

import spray.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

trait TemplateEngine {
  implicit val ec: ExecutionContext

  def loadTemplate(name: String): Future[Option[String]]

  def renderByName(data: JsValue, templateName: String): Future[Option[Throwable Either String]] = {
    loadTemplate(templateName).map(_.map(renderByValue(data, _, Some(templateName))))
  }

  def renderByValue(data: JsValue, templateValue: String, nameOpt: Option[String]): Throwable Either String
}
