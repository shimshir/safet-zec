package de.admir.safetzec

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import akka.http.scaladsl.server.Directives._
import de.admir.safetzec.rendering.RenderingService
import de.admir.safetzec.web.{ApiRoute, HttpServer, UiRoute}
import de.admir.safetzec.models.EngineEnum._
import de.admir.safetzec.rendering.dust.DustEngine
import de.admir.safetzec.rendering.freemarker.FreemarkerEngine
import de.admir.safetzec.rendering.handlebars.HandlebarsEngine
import de.admir.safetzec.templates.{H2SqlTemplateStore, InMemoryTemplateStore, MongoTemplateStore, PostgreSqlTemplateStore}

object Main extends App {
  implicit val actorSystem = ActorSystem("safet-zec")
  implicit val materializer = ActorMaterializer()
  implicit val ec = actorSystem.dispatcher

  val config: Config = ConfigFactory.load(s"application.conf")

  val httpServer = new HttpServer(config)

  val templateStore = PostgreSqlTemplateStore.herokuStore

  val supportedEngines = Map(
    FREEMARKER -> new FreemarkerEngine(),
    HANDLEBARS -> new HandlebarsEngine(),
    DUST -> new DustEngine()
  )

  val renderingService = new RenderingService(supportedEngines, templateStore)
  val apiRoute = new ApiRoute(renderingService, templateStore)
  val uiRoute = new UiRoute()

  httpServer.start(apiRoute.route ~ uiRoute.route)
}
