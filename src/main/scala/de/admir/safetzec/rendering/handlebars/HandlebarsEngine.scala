package de.admir.safetzec.rendering.handlebars

import de.admir.safetzec.rendering.RenderingEngine
import spray.json.JsObject
import com.github.jknack.handlebars.Handlebars
import de.admir.safetzec.Json2JMap

import scala.util.Try

class HandlebarsEngine() extends RenderingEngine with Json2JMap {
  private val handlebars = new Handlebars()

  def render(data: JsObject, templateValue: String, nameOpt: Option[String] = None): Throwable Either String = Try {
    val template = handlebars.compileInline(templateValue)
    template.apply(json2JMap(data))
  }.toEither
}
