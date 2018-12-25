package v1.solver

import com.quantego.clp.CLP
import com.quantego.clp.CLPConstraint
import com.quantego.clp.CLPVariable

import scala.collection.JavaConverters._

case class Resource(id: Long, name: String, measurementUnit: String, naturalProduction: Double)

case class ResourceProduction(resource: Resource, production: Double)

case class Recipe(id: Int, name: String, production: List[ResourceProduction])

case class RecipeResourceProduction(resourceProduction: ResourceProduction, recipeId: Int)

case class RecipeUtility(recipe: Recipe, utility: Double)

case class RecipeVariable(recipe: Recipe, clpVariable: CLPVariable)

case class RecipeSolution(recipeName: String, solution: Double)

case class SolverResult(objectiveValue: Double, recipeSolutions: Seq[RecipeSolution])


/**
  * Solver
  */
class Solver {


  def solve(recipes: Seq[Recipe], utilities: Seq[RecipeUtility]): SolverResult = {

    val model = new CLP().verbose(1)
    val recipeVariables = recipes.map(recipe => RecipeVariable(recipe, model.addVariable()))

    // Figure out how many resources there are in the data. If the number is readily available elsewhere, make it an input of solve and remove this.
    
    var nResources : Int = 0

    recipes.foreach(recipe => 
      recipe.production.foreach(resourceProduction =>
        if (resourceProduction.resource.id > nResources) (
          nResources = resourceProduction.resource.id
        )
      )
    )

    // Turn the incoming case classes into an array of resources. Generate a list of natural productions (again, replace with an input list of natural productions if convenient).
        
    var transposed : Array[List[(Int, Double)]] = Array.fill(nResources)(List[(Int, Double)]())
    var naturalProduction : Array[Double] = Array.fill(nResources)(0)

    recipes.foreach(recipe => 
      recipe.production.foreach{resourceProduction =>
        transposed(resourceProduction.resource.id-1) = (recipe.id, resourceProduction.production) :: transposed(resourceProduction.resource.id-1)
        naturalProduction(resourceProduction.resource.id-1) = resourceProduction.resource.naturalProduction
      }
    )

    // Input constraints.

    val constPairs = transposed.zip(naturalProduction)

    constPairs.foreach{ pair =>
      val constMap : Map[CLPVariable,java.lang.Double] = pair._1.map(production => (recipeVariables(production._1-1).clpVariable,scala.Double.box(production._2))).toMap
      val constraint = model.addConstraint(constMap.asJava,CLPConstraint.TYPE.GEQ,-pair._2)
    }

    // Set the objective.
    val objecPairs = recipeVariables.zip(utilities)
    objecPairs.foreach(p => inputObjec(p._1.clpVariable, p._2.utility))
    def inputObjec(b:CLPVariable, a:Double) : Unit = {
      model.setObjectiveCoefficient(b, a)
    }

    // Set variable bounds.
    recipeVariables.foreach(_.clpVariable.lb(0))

    // Solve the model.
    model.maximize()

    // Return results
    val recipeSolutions = recipeVariables.map( r =>
      RecipeSolution(r.recipe.name, model.getSolution(r.clpVariable))
    )
    SolverResult(
      objectiveValue = model.getObjectiveValue,
      recipeSolutions = recipeSolutions
    )
  }

  def testDataOne : ((List[Recipe],List[RecipeUtility])) = {
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
          production = 3,
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
        )
      )
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
      )
    )

    val flowerConsumptionRecipe = Recipe(
      id = 4,
      name = "Flower Consumption",
      production = List(
        ResourceProduction(
          resource = flowerResource,
          production = -1,
        )
      )
    )

    val recipes = List(freezingRecipe, iceConsumptionRecipe, flowerGrowingRecipe, flowerConsumptionRecipe)

    val utilities = List(
      RecipeUtility(
        recipe = freezingRecipe,
        utility = 0
      ),
      RecipeUtility(
        recipe = iceConsumptionRecipe,
        utility = 1
      ),
      RecipeUtility(
        recipe = flowerGrowingRecipe,
        utility = 0
      ),
      RecipeUtility(
        recipe = flowerConsumptionRecipe,
        utility = 2
      )
    )

  (recipes,utilities)

  }

  def testDataTwo : ((List[Recipe],List[RecipeUtility])) = {
    // Resources
    val waterResource = Resource(id = 1, name = "Water", measurementUnit = "Cup", naturalProduction = 1)
    val iceResource = Resource(id = 2, name = "Ice", measurementUnit = "Cube", naturalProduction = 0)
    val potTimeResource = Resource(id = 3, name = "Pot Time", measurementUnit = "Pot Month", naturalProduction = 1)
    val flowerResource = Resource(id = 4, name = "Flower", measurementUnit = "Item", naturalProduction = 0)
    val carbonDioxideResource = Resource(id = 5, name = "Carbon Dioxide", measurementUnit="Gram", naturalProduction = 300)

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
          production = 3,
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
        )
      )
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
        ResourceProduction(
          resource = carbonDioxideResource,
          production = -100
        )
      )
    )

    val flowerConsumptionRecipe = Recipe(
      id = 4,
      name = "Flower Consumption",
      production = List(
        ResourceProduction(
          resource = flowerResource,
          production = -1,
        )
      )
    )

    val recipes = List(freezingRecipe, iceConsumptionRecipe, flowerGrowingRecipe, flowerConsumptionRecipe)

    val utilities = List(
      RecipeUtility(
        recipe = freezingRecipe,
        utility = 0
      ),
      RecipeUtility(
        recipe = iceConsumptionRecipe,
        utility = 1
      ),
      RecipeUtility(
        recipe = flowerGrowingRecipe,
        utility = 0
      ),
      RecipeUtility(
        recipe = flowerConsumptionRecipe,
        utility = 2
      )
    )

  (recipes,utilities)

  }
}
