package de.admir.safetzec

import com.fasterxml.jackson.databind.ObjectMapper
import spray.json.JsValue
import java.util.{Map => JMap}

trait Json2JMap {
  import de.admir.safetzec.Json2JMap.jacksonObjectMapper

  def json2JMap(jsValue: JsValue): JMap[String, Object] = {
    val jsonStr = jsValue.compactPrint
    jacksonObjectMapper.readValue(jsonStr, classOf[JMap[String, Object]])
  }
}

object Json2JMap {
  private val jacksonObjectMapper = new ObjectMapper()
}
