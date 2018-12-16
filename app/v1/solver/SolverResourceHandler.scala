package v1.solver

import javax.inject.{Inject, Provider}
import play.api.MarkerContext
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  * DTO for displaying post information.
  */
case class SolverResource(id: String, link: String, title: String, body: String)

object SolverResource {

  /**
    * Mapping to write a PostResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[SolverResource] {
    def writes(solverResource: SolverResource): JsValue = {
      Json.obj(
        "id" -> solverResource.id,
        "link" -> solverResource.link,
        "title" -> solverResource.title,
        "body" -> solverResource.body
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[SolverResource]]
  */
class SolverResourceHandler @Inject()(
                                       routerProvider: Provider[SolverRouter],
                                       solverRepository: SolverRepository)(implicit ec: ExecutionContext) {

  def create(solverInput: SolverFormInput)(implicit mc: MarkerContext): Future[SolverResource] = {
    val data = SolverData(SolverId("999"), solverInput.title, solverInput.body)
    // We don't actually create the post, so return what we have
    solverRepository.create(data).map { id =>
      createSolverResource(data)
    }
  }

  def lookup(id: String)(implicit mc: MarkerContext): Future[Option[SolverResource]] = {
    val resourceFuture = solverRepository.get(SolverId(id))
    resourceFuture.map { maybeResourceData =>
      maybeResourceData.map { resourceData =>
        createSolverResource(resourceData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[SolverResource]] = {
    solverRepository.list().map { dataList =>
      dataList.map(data => createSolverResource(data))
    }
  }

  private def createSolverResource(p: SolverData): SolverResource = {
    SolverResource(p.id.toString, routerProvider.get.link(p.id), p.title, p.body)
  }

}
