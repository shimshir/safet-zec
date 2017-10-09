package de.admir.safetzec.rendering.freemarker

import org.scalatest._
import org.scalatest.EitherValues._
import org.scalatest.mockito.MockitoSugar
import spray.json._


class FreemarkerEngineSpec extends FlatSpec with Matchers with MockitoSugar {

  "render" should "render the given data and template" in {
    val fmEngine = new FreemarkerEngine()

    val data =
      """
        |{
        |  "name" : "Admir",
        |  "newMessageAmount": 3,
        |  "messages": ["Whats up?", "You won the lottery!", "Lunch at 2 pm"]
        |}
      """.stripMargin.parseJson.asJsObject

    val template =
      """|Hello ${name}, you have ${newMessageAmount} new messages. They are:
         |<#list messages as msg>
         |  ${msg}
         |</#list>
      """.stripMargin

    val result = fmEngine.render(data, template)
    result shouldBe a[Right[_, String]]
    val renderedStr = result.right.value
    renderedStr.split("\n").map(_.trim) should contain inOrderOnly("Hello Admir, you have 3 new messages. They are:", "Whats up?", "You won the lottery!", "Lunch at 2 pm")
  }
}
