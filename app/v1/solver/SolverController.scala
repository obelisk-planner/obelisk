package v1.solver

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

case class SolverFormInput(title: String, body: String)

class SolverController  @Inject()(cc: SolverControllerComponents)(implicit ec: ExecutionContext)
  extends SolverBaseController(cc) {

  private val logger = Logger(getClass)

  // not used right now
  def index: Action[AnyContent] = SolverAction.async { implicit request =>
    logger.trace("index: ")

    solverResourceHandler.find.map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def solverResult: Action[AnyContent] = SolverAction.async { implicit request =>
    logger.trace("solverResult: ")

    val planner = new Planner()
    val result = planner.example()
    logger.info(s"result is $result")

    Future(Ok(result))
  }

}
