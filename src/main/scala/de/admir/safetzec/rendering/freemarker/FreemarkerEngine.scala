package de.admir.safetzec.rendering.freemarker

import java.io.{StringReader, StringWriter}

import de.admir.safetzec.Json2JMap
import de.admir.safetzec.rendering.RenderingEngine
import freemarker.template.{Configuration, Template, Version}
import spray.json._

import scala.util.Try

class FreemarkerEngine() extends RenderingEngine with Json2JMap {
  private val fmCfg = new Configuration(new Version(2, 3, 23))

  def render(data: JsObject, templateStr: String, nameOpt: Option[String] = None): Throwable Either String = {
    val template = new Template(nameOpt.getOrElse("placeholderName"), new StringReader(templateStr), fmCfg)
    val stringWriter = new StringWriter()
    val result = Try {
      template.process(json2JMap(data), stringWriter)
      stringWriter.toString
    }.toEither
    stringWriter.close()
    result
  }
}
