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

    // TODO: hardcoded examples, remove these later

    // Resources
    val waterResource = Resource(id = 1, name = "Water", measurementUnit = "Cup")
    val iceResource = Resource(id = 2, name = "Ice", measurementUnit = "Cube")

    // Recipes
    val freezingRecipe = Recipe(
      id = 1,
      name = "Freezing",
      production = List(
        ResourceProduction(
          resource = waterResource,
          production = -2,
          naturalProduction = 1
        ),
        ResourceProduction(
          resource = iceResource,
          production = 3,
          naturalProduction = 0
        )
      )
    )

    val iceConsumptionRecipe = Recipe(
      id = 2,
      name = "Ice Consumption",
      production = List(
        ResourceProduction(
          resource = iceResource,
          production = -1,
          naturalProduction = 0
        )
      )
    )

    val recipes = List(freezingRecipe, iceConsumptionRecipe)

    // User defined utility. Replace with relevant data.
    val utilities = List(
      ResourceUtility(
        resource = waterResource,
        utility = 0
      ),
      ResourceUtility(
        resource = iceResource,
        utility = 1
      )
    )

    val solver = new Solver()
    val result = solver.solve(
      recipes = recipes,
      utilities = utilities
    )

    Future(Ok(s"Result is: $result"))
  }

}
