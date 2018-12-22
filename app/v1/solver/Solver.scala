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

    val resourceConstraints = getResourceConstraints(recipes)
    resourceConstraints.foreach { resourceConstraint =>
      val lhs = resourceConstraint._2.flatMap { constraint =>
        val recipeVariableOpt = recipeVariables.find(_.recipe.id == constraint.recipeId)
        recipeVariableOpt.map { recipeVariable =>
          (recipeVariable.clpVariable, scala.Double.box(constraint.resourceProduction.production))
        }
      }.toMap

      val naturalProduction = resourceConstraint._2.map(
        _.resourceProduction.resource.naturalProduction
      ).sum

      model.addConstraint(lhs.asJava, CLPConstraint.TYPE.GEQ, -naturalProduction)
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

  private def getResourceConstraints(recipes: Seq[Recipe]): Map[Resource, Seq[RecipeResourceProduction]] = {
    val constraintsList = for {
      recipe <- recipes
      constraint <- recipe.production
    } yield (constraint.resource, RecipeResourceProduction(constraint, recipe.id))
    constraintsList.groupBy(_._1).mapValues(_.map(_._2))
  }

}
