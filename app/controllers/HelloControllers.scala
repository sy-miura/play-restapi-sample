package controllers

import models.{NewStudentItem, Student}
import play.api.libs.json._
import play.api.mvc._

import javax.inject._
import scala.collection.mutable

@Singleton
class HelloControllers @Inject() (val controllerComponents: ControllerComponents)
extends BaseController {
  implicit val studentListJson = Json.format[Student]
  implicit val newStudentItem = Json.format[NewStudentItem]

  val studentList = new mutable.ListBuffer[Student]()
  studentList += Student(1,"Delhi","Mayank")
  studentList += Student(2,"Mathura","Yashika")

  def getAll(): Action[AnyContent] = Action {
    if (studentList.isEmpty) NoContent else Ok(Json.toJson(studentList))
  }

  def getById(studentId:Int) : Action[AnyContent] = Action{
      val stdId = studentList.find(_.id == studentId)
      stdId match {
        case Some(value) => Ok(Json.toJson(value))
        case None => NotFound
      }
  }

  def addNewItem(): Action[JsValue] = Action(parse.json) { implicit request =>
    request.body.validate[NewStudentItem].asOpt
      .fold{
        BadRequest("No item added")
      }
      {
        response =>
          val nextId = studentList.map(_.id).max +1
          val newItemAdded = Student(nextId,response.address,response.name)
          studentList += newItemAdded
          Ok(Json.toJson(studentList))
      }
  }
}
