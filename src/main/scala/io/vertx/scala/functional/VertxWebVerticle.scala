package io.vertx.scala.functional

import io.vertx.core.http.HttpMethod
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.{CookieHandler, CorsHandler, SessionHandler}
import io.vertx.scala.ext.web.sstore.LocalSessionStore

import scala.concurrent.Future

/**
  * The verticle designed for web services.
  *
  * @author Eric Zhao
  */
abstract class VertxWebVerticle extends ScalaVerticle {

  protected def createServer(router: Router, port: Int): Future[Unit] = {
    vertx.createHttpServer
      .requestHandler(router.accept _)
      .listenFuture(port)
      .map(_ => ())
  }

  protected def enableCorsSupport(router: Router): Unit = {
    val allowedHeaders = scala.collection.mutable.HashSet("x-requested-with", "Access-Control-Allow-Origin",
      "origin", "Content-Type", "accept")
    router.route().handler(CorsHandler.create("*")
      .allowedHeaders(allowedHeaders)
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.DELETE)
      .allowedMethod(HttpMethod.PATCH)
      .allowedMethod(HttpMethod.OPTIONS)
    )
  }

  protected def enableLocalSession(router: Router, name: String) {
    router.route.handler(CookieHandler.create)
    router.route.handler(SessionHandler.create(LocalSessionStore.create(vertx, name)))
  }
}
