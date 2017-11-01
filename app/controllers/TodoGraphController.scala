package controllers


import javax.inject.Inject

import models.{SchemaDefenitions, TodoRepository}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import sangria.execution.deferred.DeferredResolver
import sangria.execution._
import sangria.parser.{Document, QueryParser, SyntaxError}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class TodoGraphController   @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  def graphql = Action.async(parse.json){ request=>
    val query=(request.body \ "query").as[String]
    val operation =(request.body \"operationName").asOpt[String]
    val variables=(request.body \ "variables").toOption.map{
      case obj:JsObject =>obj
      case _=> Json.obj()
    }
    executeQuery(query, variables,operation )
  }


  import sangria.marshalling.playJson._

  private def executeQuery(query: String, variables: Option[JsObject], operation: Option[String]) =
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        Executor.execute(SchemaDefenitions.TodoSchema, queryAst, new TodoRepository,
          operationName = operation,
          variables = variables getOrElse Json.obj(),
          deferredResolver = DeferredResolver.fetchers(SchemaDefenitions.todos),
          exceptionHandler = exceptionHandler,
          queryReducers = List(
            QueryReducer.rejectMaxDepth[TodoRepository](15),
            QueryReducer.rejectComplexQueries[TodoRepository](4000, (_, _) ⇒ TooComplexQueryError)))
          .map(Ok(_))
          .recover {
            case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
            case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
          }

      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) ⇒
        Future.successful(BadRequest(Json.obj(
          "syntaxError" → error.getMessage,
          "locations" → Json.arr(Json.obj(
            "line" → error.originalError.position.line,
            "column" → error.originalError.position.column)))))

      case Failure(error) ⇒
        throw error
    }



  lazy val exceptionHandler = ExceptionHandler {
    case (_, error @ TooComplexQueryError) ⇒ HandledException(error.getMessage)
    case (_, error @ MaxQueryDepthReachedError(_)) ⇒ HandledException(error.getMessage)
  }

  case object TooComplexQueryError extends Exception("Query is too expensive.")

}
