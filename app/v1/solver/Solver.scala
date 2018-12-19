package v1.solver

import com.quantego.clp.CLP
import com.quantego.clp.CLPConstraint
import com.quantego.clp.CLPConstraint.TYPE
import com.quantego.clp.CLPVariable
import scala.collection.JavaConverters._

class Solver {

  def solve(nRecipes: Int,
            constraints: List[List[(Int,Double)]],
            naturalProduction: List[Double],
            utility: List[Double]
           ): String = {

    val model = new CLP().verbose(1)
    val variables = List.fill(nRecipes)(model.addVariable())
    
    // Set the constraints in the model.
    val constPairs = constraints.zip(naturalProduction)
    def handlePairs(p:(List[(Int,Double)],Double)) : Unit = {
        val constMap : Map[CLPVariable,java.lang.Double] = p._1.map(x => (variables.apply(x._1), scala.Double.box(x._2))).toMap
        model.addConstraint(constMap.asJava, CLPConstraint.TYPE.GEQ,-p._2)
    }
    constPairs.foreach(handlePairs)
    
    // Set the objective.
    val objecPairs = variables.zip(utility)
    def inputObjec(b:CLPVariable, a:Double) : Unit = {
        model.setObjectiveCoefficient(b, a)
    }
    objecPairs.foreach(p => inputObjec(p._1,0-p._2))

    // Set variable bounds.
    variables.foreach(_.lb(0))

    // Solve the model.
    model.minimize()
    model.toString
  }


}
