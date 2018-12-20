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

In the example data, there's two resources (let's call them water and ice) and two recipes (which we'll call freezing and ice consumption).
Under the constraints entry for water, there's (0,-2).
That 0 corresponds to the first recipe, freezing.
What this says is that freezing consumes two units (let's say cups) of water.
Note that the water entry in naturalProduction has a one, which corresponds to water being generated at a rate of one cup per unit time, perhaps from a rain barrel.
In the constraints entry for ice, there's (0,3) and (1, -1). What the first says is that recipe 0, freezing, produces three units (cubes) of ice. The second says that recipe 1, ice consumption, consumes one cube of ice.
Note that ice consumption has an entry of one in utility, indicating that we like having ice cubes. The algorithm will put a weight of one on maximizing ice consumption.
The solution generated is an intensity of 0.5 for freezing and 1.5 for consumption. In practice, this would translate to freezing one cup of water every two units of time, and consuming one ice cube one unit of time and two the next.


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