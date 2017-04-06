package io.vertx.scala.functional.utils

import io.vertx.core.Handler
import io.vertx.scala.ext.web.{Route, RoutingContext}
import io.vertx.scala.functional.web.Result

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.reflect.runtime.universe.{TypeTag, Type, typeOf}
import scala.util.{Failure, Success}

/**
  * Implicits global object.
  *
  * @author Eric Zhao
  */
object Implicits {
  /**
    * Resolve the result and apply to Vert.x Web.
    *
    * @param result  HTTP result entity
    * @param context routing context in Vert.x Web
    */
  private def resolveResult(result: Result)(implicit context: RoutingContext): Unit = {
    val response = context.response()
    response.setStatusCode(result.statusCode)
    for ((k, v) <- result.headers) {
      response.putHeader(k, v)
    }
    response.end(result.result)
  }

  private def resultToHandlerFunc(f: RoutingContext => Result): RoutingContext => Unit = implicit context => {
    resolveResult(f(context))
  }

  /**
    * Implicit converter function which converts the routing function to Handler.
    *
    * @param f routing function
    * @return wrapped Handler
    */
  implicit def resultToHandler(f: RoutingContext => Result): Handler[RoutingContext] = implicit context => {
    resultToHandlerFunc(f)(context)
  }

  private def resultToHandlerAsyncFunc(f: RoutingContext => Future[Result])(implicit executionContext: ExecutionContext): RoutingContext => Unit = implicit context => {
    f(context) onComplete {
      case Success(res) => resolveResult(res)
      case Failure(ex) => context.fail(ex)
    }
  }

  /**
    * Implicit converter function which converts the asynchronous routing function to Handler.
    *
    * @param f routing function returning an asynchronous HTTP result
    * @return wrapped Handler
    */
  implicit def resultToHandlerAsync[T](f: RoutingContext => Future[Result])
                                      (implicit executionContext: ExecutionContext): Handler[RoutingContext] = implicit routingContext => {
    resultToHandlerAsyncFunc(f)(executionContext)(routingContext)
  }

  /**
    * Implicit DSL adapter for Route object.
    */
  implicit class RouteImplicitConverter(route: Route) {

    /**
      * Handle the given route with the routing function asynchronously, i.e. `handleWithAsync`.
      *
      * @param f routing function
      */
    def >>>(f: RoutingContext => Future[Result])(implicit executionContext: ExecutionContext): Route = route.handler(resultToHandlerAsync(f))

    /**
      * Handle the given route with the routing function, i.e. `handleWith`.
      *
      * @param f routing function
      */
    def >>(f: RoutingContext => Result): Route = route.handler(resultToHandler(f))
  }

  /**
    * Strict type checker and converter using Scala reflection.
    *
    * @author Eric Zhao
    */
  object TypeConverter {

    // Strict type check (without type erase).
    implicit class TypeCov[T: TypeTag](x: T) {

      def is[T2: TypeTag]: Boolean = typeOf[T] =:= typeOf[T2]

      def as[T2]: T2 = x.asInstanceOf[T2]

      def getType: Type = typeOf[T]
    }

  }

}
