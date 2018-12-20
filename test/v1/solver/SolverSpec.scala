package v1.solver

import org.scalatest.FlatSpec

class SolverSpec extends FlatSpec {

  "Solver" should "solve basic example" in {
    // nRecioes to be replaced with the number of resource types in the OCE data.
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


    val solver = new Solver()
    solver.solve(
      nRecipes = nRecipes,
      constraints = constraints,
      naturalProduction = naturalProduction,
      utility = utility
    )
  }

  "Solver" should "solve another basic example" in {
    // nRecioes to be replaced with the number of resource types in the OCE data.
    val nRecipes = 20
    // constraints to be generated from OCE recipes. This is a list (with an entry for each resource type)
    // of lists of 2-tuples, each of the format (recipe index, resource production).
    // Resource production indicates consumption when negative.
    val constraints : List[List[(Int,Double)]] = List(
      List( (0,-2) ),
      List( (0,3), (1,-1) ),
      List( (0,6), (1,5) )
    )

    // naturalProduction to be replaced with relevent data. I'm not actually sure what this corresponds to
    // in OCE. Suppliers, maybe? Hopefully something.
    val naturalProduction : List[Double] = List(1,0)
    // User defined utility. Replace with relevant data.
    val utility : List[Double] = List(0,2)


    val solver = new Solver()
    solver.solve(
      nRecipes = nRecipes,
      constraints = constraints,
      naturalProduction = naturalProduction,
      utility = utility
    )
  }

}
