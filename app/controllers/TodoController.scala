package controllers

import javax.inject.Inject

import models.{Todo, TodoRepository}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, ControllerComponents}
import reactivemongo.bson.BSONObjectID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



class TodoController @Inject()(cc:ControllerComponents,todoRepository: TodoRepository) extends AbstractController(cc){



  def getAllTodos = Action.async {
    todoRepository.getAll.map{ todos =>
      Ok(Json.toJson(todos))
    }
  }

  def createTodo() = Action.async(parse.json){ req =>
    req.body.validate[Todo].map{ todo =>
      todoRepository.addTodo(todo).map{ _ =>
        Created
      }
    }.getOrElse(Future.successful(BadRequest("Invalid Todo format")))
  }

  def updateTodo(
                 todoId: BSONObjectID) = Action.async(parse.json){ req =>
    req.body.validate[Todo].map{ todo =>
      todoRepository.updateTodo(todoId, todo).map {
        case Some(todo) => Ok(Json.toJson(todo))
        case None => NotFound
      }
    }.getOrElse(Future.successful(BadRequest("Invalid Json")))
  }

  def deleteTodo(todoId: BSONObjectID) = Action.async{ req =>
    todoRepository.deleteTodo(todoId).map {
      case Some(todo) => Ok(Json.toJson(todo))
      case None => NotFound
    }
  }

}
