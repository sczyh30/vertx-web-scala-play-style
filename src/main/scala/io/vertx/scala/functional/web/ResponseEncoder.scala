package io.vertx.scala.functional.web

import io.vertx.core.buffer.Buffer
import io.vertx.lang.scala.json.{JsonArray, JsonObject}

/**
  * Encoder trait for HTTP responses.
  *
  * @author Eric Zhao
  */
trait ResponseEncoder[C] {

  /**
    * Encode the content to buffer.
    *
    * @param c content
    * @return encoded content in byte stream
    */
  def encode(c: C): Buffer
}

/**
  * Some out-of-box encoders.
  */
object Encoders {
  implicit val TextEnc: ResponseEncoder[String] = s => Buffer.buffer(s)
  implicit val VertxBufferEnc: ResponseEncoder[Buffer] = b => b
  implicit val JsonEnc: ResponseEncoder[JsonObject] = new JsonEncoder
  implicit val JsonArrayEnc: ResponseEncoder[JsonArray] = new JsonArrayEncoder
}
