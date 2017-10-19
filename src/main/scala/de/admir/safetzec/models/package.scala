package de.admir.safetzec

import de.admir.safetzec.models.EngineEnum.EngineEnum
import spray.json.JsonParser.ParsingException
import spray.json._
import reactivemongo.bson._

import scala.util.{Failure, Success, Try}

package object models {

  object EngineEnum extends Enumeration {
    type EngineEnum = Value
    val FREEMARKER: EngineEnum = Value("FREEMARKER")
    val HANDLEBARS: EngineEnum = Value("HANDLEBARS")
    val DUST: EngineEnum = Value("DUST")
  }

  case class TemplateData(name: Option[String], value: Option[String], engine: Option[EngineEnum])

  case class RenderRequest(data: JsObject, template: TemplateData)

  case class TemplateModel(name: String, value: String, engine: EngineEnum)

  import scalikejdbc._

  object TemplateModel extends SQLSyntaxSupport[TemplateModel] {
    //def apply(rs: WrappedResultSet) =
    // TemplateModel(rs.string("name"), rs.string("value"), EngineEnum.withName(rs.string("engine")))
    def apply(c: SyntaxProvider[TemplateModel])(rs: WrappedResultSet): TemplateModel =
      apply(c.resultName)(rs)
    def apply(c: ResultName[TemplateModel])(rs: WrappedResultSet): TemplateModel =
      TemplateModel(rs.string(c.name), rs.string(c.value), EngineEnum.withName(rs.string(c.engine)))
  }


  object JsonProtocols extends DefaultJsonProtocol {
    implicit lazy val engineEnumFormat = new RootJsonFormat[EngineEnum] {
      def write(obj: EngineEnum) = JsString(obj.toString)
      def read(json: JsValue) = Try(EngineEnum.withName(json.convertTo[String].toUpperCase)) match {
        case Success(jsValue) => jsValue
        case Failure(_) =>
          throw new ParsingException(s"Unsupported engine: ${json.convertTo[String]}")
      }
    }
    implicit lazy val templateDataFormat = jsonFormat3(TemplateData)
    implicit lazy val renderRequestFormat = jsonFormat2(RenderRequest)

    implicit lazy val templateModelFormat = jsonFormat3(TemplateModel.apply)
  }

  object MongoProtocols {
    implicit lazy val engineEnumHandler = new BSONHandler[BSONValue, EngineEnum] {
      def read(bson: BSONValue): EngineEnum = EngineEnum.withName(bson.asInstanceOf[BSONString].value.toUpperCase)
      def write(t: EngineEnum): BSONValue = BSONString(t.toString)
    }
    implicit lazy val personHandler: BSONDocumentHandler[TemplateModel] = Macros.handler[TemplateModel]
  }

}
