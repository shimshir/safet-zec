package de.admir.safetzec.rendering.freemarker

import de.admir.safetzec.rendering.TemplateStore
import org.scalatest._
import org.scalatest.EitherValues._
import org.scalatest.mockito.MockitoSugar
import spray.json._
import org.mockito.Mockito.when

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class FreemarkerEngineSpec extends FlatSpec with Matchers with MockitoSugar {

  "renderByValue" should "render the given data and template" in {
    val fmEngine = new FreemarkerEngine(mock[TemplateStore])

    val data =
      """
        |{
        |  "name" : "Admir",
        |  "newMessageAmount": 3,
        |  "messages": ["Whats up?", "You won the lottery!", "Lunch at 2 pm"]
        |}
      """.stripMargin.parseJson

    val template =
      """|Hello ${name}, you have ${newMessageAmount} new messages. They are:
         |<#list messages as msg>
         |  ${msg}
         |</#list>
      """.stripMargin

    val result = fmEngine.renderByValue(data, template)
    result shouldBe a [Right[_, String]]
    val renderedStr = result.right.value
    renderedStr.split("\n").map(_.trim) should contain inOrder("Hello Admir, you have 3 new messages. They are:", "Whats up?", "You won the lottery!", "Lunch at 2 pm")
  }

  "renderByName" should "load the template by name and render it with the given data" in {
    val templateStoreMock = mock[TemplateStore]
    val tmplName = "myTemplate"
    when(templateStoreMock.findTemplate(tmplName)).thenReturn(Future.successful(Some("Hello ${firstName} ${lastName}.")))
    val fmEngine = new FreemarkerEngine(templateStoreMock)

    val resultFut = fmEngine.renderByName(JsObject("firstName" -> JsString("Admir"), "lastName" -> JsString("Memic")), tmplName)

    val result = Await.result(resultFut, 2.seconds)
    result shouldBe Some(Right("Hello Admir Memic."))
  }
}
