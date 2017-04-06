package io.vertx.scala.functional.web

import io.vertx.core.buffer.Buffer
import io.vertx.lang.scala.json.{JsonArray, JsonObject}

/**
  * JSON entity encoder for HTTP responses.
  *
  * @author Eric Zhao
  */
class JsonEncoder extends ResponseEncoder[JsonObject] {
  override def encode(json: JsonObject) = Buffer.buffer(json.encodePrettily)
}

/**
  * JSON array encoder for HTTP responses.
  *
  * @author Eric Zhao
  */
class JsonArrayEncoder extends ResponseEncoder[JsonArray] {
  override def encode(jsonArr: JsonArray) = Buffer.buffer(jsonArr.encodePrettily)
}
