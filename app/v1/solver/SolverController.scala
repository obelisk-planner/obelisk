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

    val planner = new Solver()

    // TODO: hardcoded examples, remove these later
    // nResurces to be replaced with the number of resource types in the OCE data.
    val nRecipes = 2
    // constraints to be generated from OCE recipes. This is a list (with an entry for each resource type)
    // of lists of 2-tuples, each of the format (recipe index, resource production).
    // Resource production indicates consumption when negative.
    val constraints : List[List[(Int,Double)]] = List(List((0,-2)),List((0,3),(1,-1)))

    // naturalProduction to be replaced with relevent data. I'm not actually sure what this corresponds to
    // in OCE. Suppliers, maybe? Hopefully something.
    val naturalProduction : List[Double] = List(1,0)
    // User defined utility. Replace with relevant data.
    val utility : List[Double] = List(0,1)

    val result = planner.solve(
      nRecipes = nRecipes,
      constraints = constraints,
      naturalProduction = naturalProduction,
      utility = utility
    )
    logger.info(s"result is $result")

    Future(Ok(result))
  }

}
