package com.example.bridgefour.spren.programs

import cats.Monad
import cats.effect.Sync
import com.example.bridgefour.shared.background.BackgroundWorker
import com.example.bridgefour.shared.jobs.JobCreator
import com.example.bridgefour.shared.models.Config.SprenConfig
import com.example.bridgefour.shared.models.Status.ExecutionStatus
import com.example.bridgefour.shared.types.Typeclasses.ThrowableMonadError
import org.typelevel.log4cats.Logger
import com.example.bridgefour.shared.models.Task.AssignedTaskConfig
import com.example.bridgefour.shared.models.IDs.{SlotId, SlotIdTuple, TaskId}
import com.example.bridgefour.shared.models.Job.BackgroundTaskState
import com.example.bridgefour.shared.models.States.{SlotState, TaskState}
import cats.implicits.*

// A task executor service mains state internally, usually by maintaining a BackgroundWorker[F, TaskState, SlotState]
trait TaskExecutor[F[_]] {
  def start(tasks: List[AssignedTaskConfig]): F[Map[TaskId, ExecutionStatus]]
  def getSlotState(id: SlotId): F[SlotState]
  def getStatus(id: SlotId): F[ExecutionStatus]
}

object TaskExecutorService {
  def make[F[_]: ThrowableMonadError: Sync: Monad: Logger](
    sCfg: SprenConfig,
    bg: BackgroundWorker[F, BackgroundTaskState, TaskId],
    jc: JobCreator[F]
  ): TaskExecutor[F] = new TaskExecutor[F]:
    val err: ThrowableMonadError[F] = implicitly[ThrowableMonadError[F]]

    private def startTask(cfg: AssignedTaskConfig): F[(TaskId, ExecutionStatus)] = {
      val task = jc.makeJob(cfg)
      for {
        _ <- Logger[F].debug(s"Starting worker task $task in slot ${cfg.slotId}")
        r <- err.handleError(bg.start(cfg.slotId.id, task.run(), Some(cfg.taskId.id)))(_ => ExecutionStatus.Error)
                .map(s => (cfg.taskId.id, s))
        _ <- Logger[F].info(s"Started worker task $task in slot ${cfg.slotId}: $r")
      } yield r
    }

    override def start(tasks: List[AssignedTaskConfig]): F[Map[TaskId, ExecutionStatus]] =
      tasks.traverse(c => startTask(c)).map(_.toMap)

    override def getSlotState(id: SlotId): F[SlotState] =
      bg.probeResult(id, sCfg.probingTimeout).map { r =>
        r.res match
          case Right(res) => SlotState(id, status = res.status)
          case Left(status) => SlotState(id, status = status)
      }

    override def getStatus(id: SlotId): F[ExecutionStatus] =
      getSlotState(id).map(_.status)

}