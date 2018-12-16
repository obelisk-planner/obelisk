#Obelisk Planner

This is written in Scala using Play framework.

Current repo is just a copy of their Rest API sample project: https://github.com/playframework/play-scala-rest-api-example

There's also a tutorial going through their example: https://developer.lightbend.com/guides/play-rest-api/

Their example code is still left in place, and I just copied their "post" as "solver" and modified it a bit.

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
obj: + x_0 + x_1 + x_2 + x_3
Subject To
ctr_0: + x_0 <= 0.0
ctr_1: + x_1 <= 0.0
ctr_2: + x_2 <= 0.25
ctr_3: + x_3 <= 0.25
Bounds
End
```