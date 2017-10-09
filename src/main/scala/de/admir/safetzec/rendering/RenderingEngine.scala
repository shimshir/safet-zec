package de.admir.safetzec.rendering

import spray.json.JsObject

trait RenderingEngine {
  def render(data: JsObject, templateValue: String, nameOpt: Option[String]): Throwable Either String
}
