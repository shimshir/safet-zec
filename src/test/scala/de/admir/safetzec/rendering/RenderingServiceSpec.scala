package de.admir.safetzec.rendering

import de.admir.safetzec.models.EngineEnum.FREEMARKER
import de.admir.safetzec.models.{RenderRequest, TemplateData}
import de.admir.safetzec.templates.TemplateStore
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.when
import spray.json.JsObject
import org.scalatest.EitherValues._

import scala.concurrent.Await
import scala.concurrent.duration._

class RenderingServiceSpec extends FlatSpec with Matchers with MockitoSugar {
  import de.admir.safetzec.Commons.ec

  "render" should "return a rendered template" in {
    val renderingEngineMock = mock[RenderingEngine]
    val supportedEngines = Map(FREEMARKER -> renderingEngineMock)
    val renderingService = new RenderingService(supportedEngines, mock[TemplateStore])

    val jsData = JsObject()
    val templateData = TemplateData(value = Some("some template string"), engine = Some(FREEMARKER), name = None)
    val renderRequest = RenderRequest(jsData, templateData)
    val expectedRenderedText = "rendered text"
    when(renderingEngineMock.render(jsData, templateData.value.get, None)).thenReturn(Right(expectedRenderedText))

    val result = Await.result(renderingService.render(renderRequest), 1.second)
    result.get.right.value shouldBe expectedRenderedText
  }
}
