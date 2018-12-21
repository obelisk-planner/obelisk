package v1.solver

import org.scalatest.FlatSpec

class SolverSpec extends FlatSpec {

  "Solver" should "solve basic example" in {

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

    // Test
    assert(result.objectiveValue == -1.5)
    assert(result.recipeSolutions(0).solution == 0.5)
    assert(result.recipeSolutions(1).solution == 1.5)
  }

}
