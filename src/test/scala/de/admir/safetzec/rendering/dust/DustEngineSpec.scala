package de.admir.safetzec.rendering.dust

import org.scalatest._
import org.scalatest.EitherValues._
import org.scalatest.mockito.MockitoSugar
import spray.json._


class DustEngineSpec extends FlatSpec with Matchers with MockitoSugar {
  "render" should "render the given data and template" in {
    val dustEngine = new DustEngine()

    val data =
      """
        |{
        |  "name" : "Admir",
        |  "newMessageAmount": 3,
        |  "messages": ["Whats up?", "You won the lottery!", "Lunch at 2 pm"]
        |}
      """.stripMargin.parseJson.asJsObject

    val template =
      """|Hello {name}, you have {newMessageAmount} new messages. They are:
         |{#messages}{~n}
         |  {.}
         |{/messages}
      """.stripMargin.trim

    val result = dustEngine.render(data, template)
    result shouldBe a [Right[_, String]]
    val renderedStr = result.right.value
    val actualLines = renderedStr.split("\n").map(_.trim)
    actualLines should contain inOrderOnly ("Hello Admir, you have 3 new messages. They are:", "Whats up?", "You won the lottery!", "Lunch at 2 pm")
  }
}
