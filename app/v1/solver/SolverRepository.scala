package v1.solver

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class SolverData(id: SolverId, title: String, body: String)

class SolverId private(val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object SolverId {
  def apply(raw: String): SolverId = {
    require(raw != null)
    new SolverId(Integer.parseInt(raw))
  }
}


class SolverExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait SolverRepository {
  def create(data: SolverData)(implicit mc: MarkerContext): Future[SolverId]

  def list()(implicit mc: MarkerContext): Future[Iterable[SolverData]]

  def get(id: SolverId)(implicit mc: MarkerContext): Future[Option[SolverData]]
}

/**
  * A trivial implementation for the Post Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class SolverRepositoryImpl @Inject()()(implicit ec: SolverExecutionContext) extends SolverRepository {

  private val logger = Logger(this.getClass)

  private val postList = List(
    SolverData(SolverId("1"), "title 1", "value 1"),
    SolverData(SolverId("2"), "title 2", "value 2"),
    SolverData(SolverId("3"), "title 3", "value 3")
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[SolverData]] = {
    Future {
      logger.trace(s"list: ")
      postList
    }
  }

  override def get(id: SolverId)(implicit mc: MarkerContext): Future[Option[SolverData]] = {
    Future {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

  def create(data: SolverData)(implicit mc: MarkerContext): Future[SolverId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
