package io.vertx.scala.functional.web

import io.vertx.core.buffer.Buffer

import scala.language.implicitConversions


/**
  * HTTP result functional wrapper for Vert.x Web Scala.
  *
  * @param result     HTTP response body
  * @param headers    HTTP response header
  * @param statusCode HTTP response status code
  * @author Eric Zhao
  */
sealed case class Result(result: Buffer = Buffer.buffer(), headers: Map[String, String] = Map.empty, statusCode: Int = 200) {

  def withHeader(header: (String, String)): Result = {
    Result(result, Map.empty + header, statusCode)
  }

  def addHeader(header: (String, String)): Result = {
    Result(result, headers + header, statusCode)
  }

  def addHeaders(newHeaders: (String, String)*): Result = {
    Result(result, headers ++ newHeaders, statusCode)
  }

  def removeHeader(headerKey: String): Result = {
    Result(result, headers - headerKey, statusCode)
  }

  def clearHeaders: Result = {
    Result(result, Map.empty, statusCode)
  }
}

/**
  * Result wrapper class.
  *
  * @param statusCode HTTP status code
  */
class Status(statusCode: Int) extends Result(statusCode = statusCode) {
  def apply[C](content: C)(implicit encoder: ResponseEncoder[C]): Result = {
    Result(result = encoder.encode(content), statusCode = this.statusCode)
  }
}

object Results {
  val Ok = new Status(200)
  val NoContent = new Status(204)
  val BadRequest = new Status(400)
  val Unauthorized = new Status(401)
  val Forbidden = new Status(403)
  val NotFound = new Status(404)
  val InternalServerError = new Status(500)
  val NotImplemented = new Status(501)
  val BadGateway = new Status(502)
  val ServiceUnavailable = new Status(503)
  val GatewayTimeout = new Status(504)
}