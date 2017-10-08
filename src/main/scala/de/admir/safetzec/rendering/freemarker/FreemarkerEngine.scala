package de.admir.safetzec.rendering.freemarker

import java.io.{StringReader, StringWriter}
import java.util.{Map => JMap}

import com.fasterxml.jackson.databind.ObjectMapper
import de.admir.safetzec.rendering.{TemplateEngine, TemplateStore}
import freemarker.template.{Configuration, Template, Version}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class FreemarkerEngine(templateStore: TemplateStore)(override implicit val ec: ExecutionContext) extends TemplateEngine with DefaultJsonProtocol {
  private val fmCfg = new Configuration(new Version(2, 3, 23))
  private val jacksonObjectMapper = new ObjectMapper()

  def loadTemplate(name: String): Future[Option[String]] = {
    templateStore.findTemplate(name)
  }

  def renderByValue(data: JsValue, templateStr: String, nameOpt: Option[String] = None): Throwable Either String = {
    val template = new Template(nameOpt.getOrElse("placeholderName"), new StringReader(templateStr), fmCfg)
    val stringWriter = new StringWriter();
    val result = Try {
      template.process(jsValue2JMap(data), stringWriter)
      stringWriter.toString
    }.toEither
    stringWriter.close();
    result
  }

  private def jsValue2JMap(jsValue: JsValue): JMap[String, Object] = {
    val jsonStr = jsValue.compactPrint
    jacksonObjectMapper.readValue(jsonStr, classOf[JMap[String, Object]])
  }
}
