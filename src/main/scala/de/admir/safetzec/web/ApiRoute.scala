package de.admir.safetzec.web

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.admir.safetzec.models.{RenderRequest, TemplateData, TemplateModel}
import de.admir.safetzec.models.JsonProtocols._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import de.admir.safetzec.rendering.RenderingService
import de.admir.safetzec.templates.TemplateStore

class ApiRoute(renderingService: RenderingService, templateStore: TemplateStore) extends SprayJsonSupport {

  private def renderRoute: Route =
    path("render") {
      (post & entity(as[RenderRequest])) { renderRequest =>
        renderRequest.template match {
          case TemplateData(Some(_), Some(_), _) =>
            complete(StatusCodes.BadRequest, "Please provide either name or value, but not both!")
          case TemplateData(Some(_), None, Some(_)) =>
            complete(StatusCodes.BadRequest, "Please omit the engine field when referencing an existing template by name!")
          case TemplateData(None, None, _) =>
            complete(StatusCodes.BadRequest, "Please provide either the template name or value!")
          case _ =>
            respondWithHeader(RawHeader("X-Engine-Type", renderRequest.template.engine.toString)) {
              onSuccess(renderingService.render(renderRequest)) {
                case Some(Right(rendered)) => complete(rendered)
                case Some(Left(t)) => complete(StatusCodes.BadRequest, t.getMessage)
                case None => complete(StatusCodes.NotFound, s"Could not find template for name: ${renderRequest.template.name}")
              }
            }
        }
      }
    }

  private def templatesRoute: Route =
    pathPrefix("templates") {
      (get & pathEndOrSingleSlash) {
        onSuccess(templateStore.templates) { templates =>
          complete(StatusCodes.OK, templates)
        }
      } ~ (post & pathEndOrSingleSlash & entity(as[TemplateModel])) { templateModel =>
        onSuccess(templateStore.saveTemplate(templateModel)) { _ =>
          complete(StatusCodes.Created)
        }
      } ~ (get & path(Segment)) { name =>
        onSuccess(templateStore.findTemplate(name)) {
          case Some(templateModel) => complete(StatusCodes.OK, templateModel)
          case None => complete(StatusCodes.NotFound, s"Could not find a template for name: '$name'")
        }
      }
    }

  def route: Route = {
    pathPrefix("api")(renderRoute ~ templatesRoute)
  }
}
