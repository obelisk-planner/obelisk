# Obelisk

## What?

This project attempts to provide a generic optimization platform for economic planning.

## Contributing

If you're coming here from EngSciMath Slack workspace and would like to contribute, please check the topic of #obelisk-project channel for admin contacts and request push access. Alternatively, you can fork this repo and create a pull request.

## Overview

This is written in Scala using Play framework.

Current repo is just a copy of Lightbend's Rest API sample project: https://github.com/playframework/play-scala-rest-api-example

There's also a tutorial going through their example: https://developer.lightbend.com/guides/play-rest-api/

The main logic currently is in Solver.scala file. Here's an example of how it might be used:

### Example

In the example data, there's four resources (water, ice, time in a flower pot and flowers) and four recipes (freezing, ice consumption, flower growing and flower consumption).

Each recipe has a set of paramiters; an ID, a name and a list of resource productions, which in turn contain a resource and a level of production. Take a look at the freezing recipe. it has ID zero, name "freezingRecipe" and two resource productions. The first resource production is for water, and has a production of minus two. What this says is that freezing consumes two units (cups, according to the definition of water) of water. The second resource production is for ice, and contains a production level of three, which corresponds to the recipe producing three units (cubes) of ice.

Each resource, in turn, contains an ID, a name, a unit of measurement and a natural production. Looking at the resource water, it has an ID of zero, a name of "Water", a unit of "Cup" and a natural production of one. Unit refers to the standardized unit with which to measure the resource, and nattural production refers to the rate at which the resource is produced "for free", as in without needing any inputs. The natural production of one corresponds to water being generated at a rate of one cup per unit time, perhaps from a rain barrel.

There is also recipe utilities, which indicate when nonzero some preferance for the intensity, of number of parrelel instances of a recipe. Ice consumption has a utility entry of one, indicating that we like having ice cubes. The algorithm will put a weight of one on maximizing ice consumption.

The solution generated is the intensities that maximize utility without outstripping available resources. In the example case, it's an intensity of one for ice consumption and one third for everything else. In practice, this would translate to freezing one cup of water, as well as growing and picking one flower every three months, and using one ice cube every month (it's an example).

## Running Locally

- Install SBT: https://www.scala-sbt.org/
- Clone this repo: 
> git clone git@github.com:obelisk-planner/obelisk.git
- Go to the root of newly cloned repo:
> cd obelisk
- Start SBT (this takes a minute or so):
> sbt
- Once SBT started, inside SBT console compile first:
> compile
- Once compiled, run the app:
> run
- Once ready, you can load the app in your browser at http://localhost:9000/
- Click on the link, the output should just be the result of the solver with preliminary hard-coded inputs, likes so:

```
Minimize
obj: - x_1
Subject To
ctr_0: - 2.0 x_0 >= -1.0
ctr_1: + 3.0 x_0 - x_1 >= 0.0
Bounds
End
```


And in application log you should see something like:

```
Coin0506I Presolve 0 (-2) rows, 0 (-2) columns and 0 (-3) elements
Clp3002W Empty problem - 0 rows, 0 columns and 0 elements
Clp0000I Optimal - objective value -1.5
Coin0511I After Postsolve, objective -1.5, infeasibilities - dual 0 (0), primal 0 (0)
Clp0032I Optimal objective -1.5 - 0 iterations time 0.002, Presolve 0.00

```
