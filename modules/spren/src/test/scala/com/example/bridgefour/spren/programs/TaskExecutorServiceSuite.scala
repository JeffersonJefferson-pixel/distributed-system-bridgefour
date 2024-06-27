package com.example.bridgefour.spren.programs

import cats.effect.{IO, Sync}
import com.example.bridgefour.shared.background.BackgroundWorker.FiberContainer
import com.example.bridgefour.shared.background.BackgroundWorkerService
import com.example.bridgefour.shared.models.IDs.TaskId
import com.example.bridgefour.shared.models.Job.BackgroundTaskState
import com.example.bridgefour.shared.models.Status.ExecutionStatus
import com.example.bridgefour.shared.persistence.InMemoryPersistence
import com.example.bridgefour.spren.TestUtils.*
import com.example.bridgefour.spren.TestUtils.Jobs.*
import munit.CatsEffectSuite
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class TaskExecutorServiceSuite extends CatsEffectSuite {
  implicit def unsafeLogger[F[_]: Sync]: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
  private val stateF = InMemoryPersistence.makeF[IO, Long, FiberContainer[IO, BackgroundTaskState, TaskId]]()

  test("TaskExecutorService can start a job") {
    for {
      state <- stateF
      bg = BackgroundWorkerService.make[IO, BackgroundTaskState, TaskId](state)
      srv = TaskExecutorService.make(sprenCfg, bg, FakeJobCreator())
      statusMap <- srv.start(List(sampleTask))
      _ = assertEquals(statusMap, Map(taskId -> ExecutionStatus.InProgress))
      _ <- IO.println("Getting result")
      // check storage
      res <- bg.getResult(slotId)
      _ = assertEquals(res.res.toOption.get, BackgroundTaskState(taskId, ExecutionStatus.Done))
      _ = assertEquals(res.meta.get, taskId)
      // check API
      status <- srv.getStatus(slotId)
      _ = assertEquals(status, ExecutionStatus.Done)
    } yield()
  }
}
