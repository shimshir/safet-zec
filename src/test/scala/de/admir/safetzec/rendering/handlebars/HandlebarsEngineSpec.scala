package de.admir.safetzec.rendering.handlebars

import org.scalatest._
import org.scalatest.EitherValues._
import spray.json._


class HandlebarsEngineSpec extends FlatSpec with Matchers {
  "renderByValue" should "render the given data and template" in {
    val fmEngine = new HandlebarsEngine()

    val data =
      """
        |{
        |  "name" : "Admir",
        |  "newMessageAmount": 3,
        |  "messages": ["Whats up?", "You won the lottery!", "Lunch at 2 pm"]
        |}
      """.stripMargin.parseJson.asJsObject

    val template =
      """
        |Hello {{name}}, you have {{newMessageAmount}} new messages. They are:
        |{{#messages ~}}
        |  {{this}}
        |{{/messages ~}}
      """.stripMargin.trim

    val result = fmEngine.render(data, template)
    result shouldBe a[Right[_, String]]
    val renderedStr = result.right.value
    val actualLines = renderedStr.split("\n").map(_.trim)
    actualLines should contain inOrderOnly("Hello Admir, you have 3 new messages. They are:", "Whats up?", "You won the lottery!", "Lunch at 2 pm")
  }
}
