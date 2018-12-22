package v1.solver

import org.scalatest.FlatSpec

class SolverSpec extends FlatSpec {

  "Solver" should "solve basic example" in {

    // Resources
    val waterResource = Resource(id = 1, name = "Water", measurementUnit = "Cup", naturalProduction = 1)
    val iceResource = Resource(id = 2, name = "Ice", measurementUnit = "Cube", naturalProduction = 0)
    val potTimeResource = Resource(id = 3, name = "Pot Time", measurementUnit = "Pot Month", naturalProduction = 1)
    val flowerResource = Resource(id = 4, name = "Flower", measurementUnit = "Item", naturalProduction = 0)

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
          production = 1,
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

    val solver = new Solver()
    val result = solver.solve(
      recipes = recipes,
      utilities = utilities
    )

    // Test
    assert(result.objectiveValue == -1.6666667)
    assert(result.recipeSolutions(0).solution == 0.33333333333333337))
    assert(result.recipeSolutions(1).solution == 1.0)
    assert(result.recipeSolutions(1).solution == 0.3333333333333333)
    assert(result.recipeSolutions(1).solution == 0.3333333333333333)
  }

}
