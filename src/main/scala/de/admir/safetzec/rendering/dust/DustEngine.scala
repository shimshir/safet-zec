package de.admir.safetzec.rendering.dust

import de.admir.safetzec.rendering.RenderingEngine
import spray.json.JsObject
import javax.script.{ScriptContext, ScriptEngineManager, SimpleBindings}
import java.io.StringWriter

import scala.util.Try

class DustEngine() extends RenderingEngine {
  private val scriptEngineManager = new ScriptEngineManager
  private val scriptEngine = scriptEngineManager.getEngineByName("nashorn")

  scriptEngine.eval(io.Source.fromResource("js/dust-full.js").mkString)
  scriptEngine.eval("dust.optimizers.format = function(ctx, node) { return node };")

  private val renderScript =
    """
      |{
      |  var compiledTemplate = dust.compile(templateValue, templateName);
      |  dust.loadSource(compiledTemplate);
      |  dust.render(templateName, JSON.parse(jsonData), function(err, data) {
      |    if(err) {
      |      throw new Error(err);
      |    } else {
      |      writer.write(data, 0, data.length);
      |    }
      |  });
      |}
    """.stripMargin

  def render(data: JsObject, templateValue: String, nameOpt: Option[String] = None): Throwable Either String = {
    val writer = new StringWriter
    val bindings = new SimpleBindings
    bindings.put("templateName", nameOpt.getOrElse("placeholderName"))
    bindings.put("templateValue", templateValue)
    bindings.put("jsonData", data.compactPrint)
    bindings.put("writer", writer)
    scriptEngine.getContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE)
    val result = Try(scriptEngine.eval(renderScript, scriptEngine.getContext)).toEither.map(_ => writer.toString)
    writer.close()
    result
  }
}
