package example

import io.vertx.lang.scala.VertxExecutionContext
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.{Router, RoutingContext}
import io.vertx.scala.functional.VertxWebVerticle
import io.vertx.scala.functional.utils.Implicits._
import io.vertx.scala.functional.web.Results._
import io.vertx.scala.functional.web.Result
import io.vertx.scala.functional.web.Encoders._

import scala.async.Async.{async, await}
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * The example verticle.
  *
  * @author Eric Zhao
  */
class ExampleVerticle extends VertxWebVerticle {

  // Mock service instances.
  private val authService: AuthService =
    (credential: JsonObject) => Future.successful(Json.obj("uid" -> "KKK"))
  private val userService: UserService =
    (id: String) => Future.successful(User("KKK", "+1s"))

  override def startFuture(): Future[Unit] = {
    val router = Router.router(vertx)

    router.post("/api/test/:id") >> apiTest
    router.post("/api/u") >>> apiTestAsyncUltimate
    router.get("/api/async") >>> apiTestAsync

    createServer(router, 8899)
  }

  private def apiTestAsync(context: RoutingContext): Future[Result] = {
    for {
      credential <- authService.auth(Json.obj("f" -> "x"))
      userId = credential.getString("uid")
      user <- userService.getUser(userId)
    } yield Ok(Json.obj(User.unapply(user).get)) // Body in JSON object format.
  }

  private def apiTestAsyncUltimate(context: RoutingContext): Future[Result] = {
    // Construct an `Option` for convenience.
    val rtkOps = for {
      t <- context.request().getHeader("type")
      requestToken <- context.request().getHeader("Authentication")
    } yield Json.obj("auth_type" -> t, "request_token" -> requestToken)

    // Validate request token.
    rtkOps match {
      case Some(rtk) => async {
        val credential = await {
          authService.auth(rtk)
        }
        val userId = credential.getString("uid")
        val user = await {
          userService.getUser(userId)
        }
        Ok(Json.obj(User.unapply(user).get).encodePrettily) // Body in string format.
      }
      case None => Future.successful(NotFound(Json.obj("message" -> "not_found")))
    }
  }

  private def apiTest(context: RoutingContext): Result = context.request().getParam("id") match {
    case Some(id) => id match {
      case "ha" => Ok(Json.obj("message" -> s"test OK: $id")) withHeader ("ping" -> "pong")
      case _ => BadRequest(Json.obj("message" -> s"test failure: $id")) clearHeaders
    }
    case None => BadRequest("Nothing!")
  }
}

// Runner object.

object Runner {
  def main(args: Array[String]): Unit = {
    val vertx = Vertx.vertx()
    implicit val executionContext = VertxExecutionContext(vertx.getOrCreateContext())
    vertx.deployVerticleFuture("scala:example.ExampleVerticle") onComplete {
      case Success(id) => println(s"Deployment successful (ID: $id)")
      case Failure(ex) => ex.printStackTrace()
    }
  }
}
