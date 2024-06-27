package com.example.bridgefour.spren.http

import cats.Monad
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import com.example.bridgefour.shared.models.Worker.WorkerState
import com.example.bridgefour.shared.http.Route
import org.http4s.server.Router
import cats.implicits.*
import com.example.bridgefour.spren.services.WorkerService
import io.circe.syntax.*
import org.http4s.circe.*

case class WorkerRoutes[F[_]: Monad](workerSvc: WorkerService[F]) extends Http4sDsl[F] with Route[F] {
  protected val prefixPath: String = "/worker"

  protected def httpRoutes(): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "status" => Ok()
      case GET -> Root / "state" =>
        workerSvc.state().flatMap {
          (state: WorkerState) => Ok(state.asJson)
        }
    }
  }

  def routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes()
  )
}
