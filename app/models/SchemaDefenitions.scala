package models
import models.{Todo, TodoRepository}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import sangria.schema.{Field, ObjectType}
import sangria.execution.deferred.{Fetcher, FetcherConfig, HasId}
import sangria.schema._

import scala.concurrent.Future
object SchemaDefenitions {



  val todos = Fetcher.caching(
    (ctx: TodoRepository, ids: Seq[String]) ⇒
      Future.successful(ids.map(id => ctx.getTodo(  BSONObjectID(id)))))


  val Todo =
     ObjectType(
       "Todo",
       "List of tasks for user",
       fields[Unit, Todo](
         Field("id", OptionType(StringType),
           Some("id of the todo"),
           resolve = _.value._id),
         Field("title", OptionType(StringType),
           Some("title of the post"),
           resolve = _.value.title),
         Field("status", OptionType(BooleanType),
           Some("status of the todo"),
           resolve = _.value.completed)
       ))

  val ID = Argument("id", StringType, description = "id of the character")


  val Query = ObjectType(
    "Query", fields[TodoRepository, Unit](
      Field("human", OptionType(Todo),
        arguments = ID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getTodo(BSONObjectID( ID.name)))
//      Field("droid", Droid,
//        arguments = ID :: Nil,
//        resolve = Projector((ctx, f) ⇒ ctx.ctx.getDroid(ctx arg ID).get))
    ))

  val TodoSchema = Schema(Query)


}
