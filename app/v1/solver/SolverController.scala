package v1.solver

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

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

  def solverExample: Action[AnyContent] = SolverAction.async { implicit request =>

    // TODO: hardcoded examples, remove these later

    // Resources
    val waterResource = Resource(id = 1, name = "Water", measurementUnit = "Cup", naturalProduction = 1)
    val iceResource = Resource(id = 2, name = "Ice", measurementUnit = "Cube", naturalProduction = 0)
    val potTimeResource = Resource(id = 3, name = "Pot Time", measurementUnit = "Pot Month", naturalProduction = 1)
    val flowerResource = Resource(id = 4, name = "Flower", measurementUnit = "Item", naturalProduction = 0)

    // Recipes
    val freezingRecipe = Recipe(
      id = 1,
      name = "Freezing",
      production = List(
        ResourceProduction(
          resource = waterResource,
          production = -2,
        ),
        ResourceProduction(
          resource = iceResource,
          production = 3
        )
      ),
      utility = 0
    )

    val iceConsumptionRecipe = Recipe(
      id = 2,
      name = "Ice Consumption",
      production = List(
        ResourceProduction(
          resource = iceResource,
          production = -1,
        )
      ),
      utility = 1
    )

    val flowerGrowingRecipe = Recipe(
      id = 3,
      name = "Flower Growing",
      production = List(
        ResourceProduction(
          resource = waterResource,
          production = -1,
        ),
        ResourceProduction(
          resource = potTimeResource,
          production = -3,
        ),
        ResourceProduction(
          resource = flowerResource,
          production = 1,
        ),
      ),
      utility = 0
    )

    val flowerConsumptionRecipe = Recipe(
      id = 4,
      name = "Flower Consumption",
      production = List(
        ResourceProduction(
          resource = flowerResource,
          production = -1,
        )
      ),
      utility = 2
    )

    val recipes = List(freezingRecipe, iceConsumptionRecipe, flowerGrowingRecipe, flowerConsumptionRecipe)

    val solver = new Solver()
    val result = solver.solve(recipes)

    Future(Ok(s"Result is: $result"))
  }

  // This helper parses and validates JSON using the implicit `placeReads`
  // above, returning errors if the parsed json fails validation.
  def validateJson[A : Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  implicit val resourceReads = Json.reads[Resource]
  implicit  val resourceProductionReads = Json.reads[ResourceProduction]
  implicit val recipeReads = Json.reads[Recipe]

  def process: Action[Seq[Recipe]] = Action(validateJson[Seq[Recipe]]).async { implicit request =>
    logger.trace("process: ")
    val recipes = request.body

    val solver = new Solver()
    val result = solver.solve(recipes)

    Future(Ok(s"Result is: $result"))
  }
}
