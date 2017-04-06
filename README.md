# Vert.x Web Scala Play-style extension

A Play-style functional, asynchronous extension for Vert.x Web Scala.

## Usage

First import the following packages:

```scala
import io.vertx.scala.functional.utils.Implicits._
import io.vertx.scala.functional.web.Results._
import io.vertx.scala.functional.web.Result
import io.vertx.scala.functional.web.Encoders._
```

Then you can write your routing handler methods.
In the extension, the methods will return a `Result` object (or `Future[Result]` for asynchronous handlers).
That is, the type of our handler methods will be `RoutingContext => Result`(or `RoutingContext => Future[Result]`)
instead of `RoutingContext => Unit`(i.e. `Handler[RoutingContext]` in original Vert.x Web).

The `Result` object is actually a wrapper for HTTP responses. It provides several helper methods (e.g. `withHeader` for putting HTTP header in HTTP response)
and many useful construct functions(in `Results` object).

> Documentation Unfinished.