package com.example.bridgefour.spren.http

import cats.data.Kleisli
import fs2.io.net.Network
import cats.effect.{Async, Resource}
import cats.{Monad, Parallel}
import com.example.bridgefour.shared.background.BackgroundWorker.FiberContainer
import com.example.bridgefour.shared.background.BackgroundWorkerService
import com.example.bridgefour.shared.jobs.JobCreatorService
import com.example.bridgefour.shared.models.States.{SlotState, TaskState}
import com.example.bridgefour.shared.persistence.InMemoryPersistence
import com.example.bridgefour.spren.models.Config.ServiceConfig
import org.typelevel.log4cats.Logger
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.{HttpApp, Request, Response}
import org.http4s.server.middleware.Logger as Http4sLog
import com.comcast.ip4s.{Host, Port}
import com.example.bridgefour.shared.models.Job.BackgroundTaskState
import cats.syntax.all.*
import com.example.bridgefour.shared.models.IDs.TaskId
import com.example.bridgefour.spren.models.Config.ServiceConfig
import com.example.bridgefour.spren.programs.TaskExecutorService
import com.example.bridgefour.spren.services.WorkerService
import com.example.bridgefour.spren.models.Config.ServiceConfig

object Server {
  def run[F[_] : Async : Parallel : Network : Logger](cfg: ServiceConfig): F[Nothing] = {
    val mF = implicitly[Monad[F]]
    for {
      state <- Resource.make(InMemoryPersistence.makeF[F, Long, FiberContainer[F, BackgroundTaskState, TaskId]]())(_ => mF.unit)
      bgSrv = BackgroundWorkerService.make[F, BackgroundTaskState, TaskId](state)
      jcSrv = JobCreatorService.make[F]()
      execSrv = TaskExecutorService.make[F](cfg.self, bgSrv, jcSrv)
      workerSrv = WorkerService.make[F](cfg.self, execSrv)
      httpApp: Kleisli[F, Request[F], Response[F]] = (
        TaskRoutes[F](execSrv).routes <+>
          WorkerRoutes[F](workerSrv).routes
        ).orNotFound

      // middleware
      finalHttpApp: HttpApp[F] = Http4sLog.httpApp(true, true)(httpApp)

      _ <- EmberServerBuilder
        .default[F]
        .withHost(Host.fromString(cfg.self.host).get)
        .withPort(Port.fromInt(cfg.self.port).get)
        .withHttpApp(finalHttpApp)
        .build
    } yield ()
  }.useForever
}
