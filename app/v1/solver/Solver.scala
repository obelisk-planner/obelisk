package v1.solver

import com.quantego.clp.CLP
import com.quantego.clp.CLPConstraint
import com.quantego.clp.CLPConstraint.TYPE
import com.quantego.clp.CLPVariable
import scala.collection.JavaConverters._

class Solver {

  def example(): String = {
    // nResurces to be replaced with the number of resource types in the OCE data.
    val nResources = 2
    // constraints to be generated from OCE recipes. This is a list (with an entry for each resource type)
    // of lists of 2-tuples, each of the format (recipe index, resource production).
    // Resource production indicates consumption when negative. 
    val constraints : List[List[(Int,Double)]] = List(List((0,-2)),List((0,3),(1,-1)))
    val nRecipes = constraints.length
    // naturalProduction to be replaced with relevent data. I'm not actually sure what this corresponds to
    // in OCE. Suppliers, maybe? Hopefully something.
    val naturalProduction : List[Double] = List(1,0)
    // User defined utility. Replace with relevant data.
    val utility : List[Double] = List(0,1)

    val model = new CLP().verbose(1)
    val variables = List.fill(nRecipes)(model.addVariable())
    
    // Set the constraints in the model.
    val constPairs = constraints.zip(naturalProduction)
    def handlePairs(p:(List[(Int,Double)],Double)) : Unit = {
        val constMap : Map[CLPVariable,java.lang.Double] = p._1.map(x => (variables.apply(x._1),scala.Double.box(x._2))).toMap
        val constraint = model.addConstraint(constMap.asJava,CLPConstraint.TYPE.GEQ,-p._2)
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
