package de.admir.safetzec.web

import org.scalatest._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import de.admir.safetzec.models.EngineEnum._
import de.admir.safetzec.models.{RenderRequest, TemplateData, TemplateModel}
import de.admir.safetzec.models.JsonProtocols._
import de.admir.safetzec.rendering.RenderingService
import de.admir.safetzec.templates.TemplateStore
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.when
import spray.json.JsObject

import scala.concurrent.Future

class ApiRouteSpec extends FlatSpec with Matchers with ScalatestRouteTest with MockitoSugar with SprayJsonSupport {

  "POST /api/render" should "return a rendered template" in {
    val renderingServiceMock = mock[RenderingService]
    val expectedRenderedText = "rendered text"
    val templateStoreMock = mock[TemplateStore]
    val templateData = TemplateData(value = Some("some template string"), engine = Some(FREEMARKER), name = None)
    val renderRequest = RenderRequest(data = JsObject(), template = templateData)
    when(renderingServiceMock.render(renderRequest)).thenReturn(Future.successful(Some(Right(expectedRenderedText))))

    val apiRoute = new ApiRoute(renderingServiceMock, templateStoreMock)

    Post("/api/render", renderRequest) ~> apiRoute.route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe expectedRenderedText
    }
  }

  "POST /api/templates" should "return 201 Created" in {
    val renderingServiceMock = mock[RenderingService]
    val templateStoreMock = mock[TemplateStore]
    val templateModel = TemplateModel("templateName", "templateValue", FREEMARKER)
    when(templateStoreMock.saveTemplate(templateModel)).thenReturn(Future.successful(Right("templateName")))

    val apiRoute = new ApiRoute(renderingServiceMock, templateStoreMock)

    Post("/api/templates", templateModel) ~> apiRoute.route ~> check {
      status shouldBe StatusCodes.Created
    }
  }
}
