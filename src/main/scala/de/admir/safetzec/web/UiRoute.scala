package de.admir.safetzec.web

import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route


class UiRoute extends SLF4JLogging {

  private val uiFolder = "safet-zec-ui/build"

  def route: Route =
    getFromResourceDirectory(uiFolder) ~ getFromResource(s"$uiFolder/index.html")
}
