package com.example.bridgefour.spren.http

import cats.effect.Concurrent
import com.example.bridgefour.shared.http.Route
import com.example.bridgefour.shared.models.IDs.TaskId
import com.example.bridgefour.shared.models.Status.ExecutionStatus
import com.example.bridgefour.shared.models.Task.AssignedTaskConfig
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import cats.syntax.all.*
import com.example.bridgefour.spren.programs.TaskExecutor

case class TaskRoutes[F[_]: Concurrent](executor: TaskExecutor[F])
  extends Http4sDsl[F] with Route[F] {

  protected  val prefixPath: String = "/task"

  given EntityDecoder[F, AssignedTaskConfig] = accumulatingJsonOf[F, AssignedTaskConfig]
  given EntityEncoder[F, Map[TaskId, ExecutionStatus]] = jsonEncoderOf[F, Map[TaskId, ExecutionStatus]]

  protected def httpRoutes(): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case req @ POST -> Root / "start" =>
        Ok(for {
          tasks <- req.as[List[AssignedTaskConfig]]
          res <- executor.start(tasks)
        } yield res)
    }
  }
  
  def routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes()
  )
}
