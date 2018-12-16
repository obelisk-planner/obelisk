package v1.solver

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the PostResource controller.
  */
class SolverRouter @Inject()(controller: SolverController) extends SimpleRouter {
  val prefix = "/v1/solver"

  def link(id: SolverId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.solverResult
  }

}
