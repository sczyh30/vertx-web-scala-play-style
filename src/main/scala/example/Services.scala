package example

import io.vertx.lang.scala.json.JsonObject

import scala.concurrent.Future

// Services and entities.

case class User(id: String, username: String)

trait AuthService {
  def auth(credential: JsonObject): Future[JsonObject]
}

trait UserService {
  def getUser(id: String): Future[User]
}
