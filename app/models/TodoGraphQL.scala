package models

import javax.inject.Inject

import play.api.mvc.ControllerComponents
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONObjectID
import sangria.schema._

import scala.concurrent.Future

case class TodoGraphQL(_id:Option[BSONObjectID],title:String,completed:Option[Boolean])


class TodoRepo @Inject()(todoRepository: TodoRepository) {
  def getTodo(id: String): Future[Option[Todo]] = todoRepository.getTodo(BSONObjectID(id))
  def createTodo(todo:Todo):Future[WriteResult]=todoRepository.addTodo(todo)
}