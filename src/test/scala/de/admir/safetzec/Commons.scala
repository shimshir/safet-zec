package de.admir.safetzec

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Commons {
  implicit val actorSystem = ActorSystem("test-system")
  implicit val mat = ActorMaterializer()
  implicit val ec = actorSystem.dispatcher
}
