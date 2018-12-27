package v1.solver

import com.quantego.clp.CLP
import com.quantego.clp.CLPConstraint
import com.quantego.clp.CLPVariable

import scala.collection.JavaConverters._

case class Resource(id: Int, name: String, measurementUnit: String, naturalProduction: Double)

case class ResourceProduction(resource: Resource, production: Double)

case class Recipe(id: Int, name: String, production: List[ResourceProduction], utility: Double)

case class RecipeResourceProduction(resourceProduction: ResourceProduction, recipeId: Int)

case class RecipeUtility(recipe: Recipe, utility: Double)

case class RecipeVariable(recipe: Recipe, clpVariable: CLPVariable)

case class RecipeSolution(recipeName: String, solution: Double)

case class SolverResult(objectiveValue: Double, recipeSolutions: Seq[RecipeSolution])


/**
  * Solver
  */
class Solver {


  def solve(recipes: Seq[Recipe]): SolverResult = {

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
      model.addConstraint(constMap.asJava,CLPConstraint.TYPE.GEQ,-pair._2)
    }

    // Set the objective.
    recipeVariables.foreach(p => inputObjec(p.clpVariable, p.recipe.utility))
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
}
