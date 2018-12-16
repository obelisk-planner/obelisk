package v1.solver

import com.quantego.clp.CLP

class Solver {

  def example(): String = {
    val model = new CLP().verbose(1)
    val energy = model.addVariable()
    val executionTime = model.addVariable()
    val cpuUsage = model.addVariable()
    val ramUsage = model.addVariable()

    model.createExpression.add(energy, executionTime, cpuUsage, ramUsage).asObjective
    model.createExpression.add(energy).leq(0)
    model.createExpression.add(executionTime).leq(0)
    model.createExpression.add(cpuUsage).leq(0.25)
    model.createExpression.add(ramUsage).leq(0.25)

    model.minimize()
    model.toString
  }


}
