package com.example.bridgefour.shared.jobs

import cats.effect.{Async, Temporal}
import com.example.bridgefour.shared.models.Job.BackgroundTaskState
import com.example.bridgefour.shared.models.Status.ExecutionStatus.Done
import com.example.bridgefour.shared.models.Task.AssignedTaskConfig
import org.latestbit.circe.adt.codec.JsonTaggedAdt
import org.typelevel.log4cats.Logger;

enum JobClass derives JsonTaggedAdt.Codec  {
    case SampleJob
    case DelayWordCountJob
    case AlwaysOkJob
}

trait BridgeFourJob[F[_]] {
    def jobClass: JobClass
    def config: AssignedTaskConfig
    def run(): F[BackgroundTaskState]
}

trait JobCreator[F[_]] {
    def makeJob(cfg: AssignedTaskConfig): BridgeFourJob[F]
}
object JobCreatorService {
    def make[F[_]: Async: Temporal: Logger](): JobCreator[F] = (cfg: AssignedTaskConfig) =>
      cfg.jobClass match
          case JobClass.SampleJob => SampleBridgeFourJob(cfg)
          case _ => ???
}

// does nothing but return "Done"
case class SampleBridgeFourJob[F[_]: Async](config: AssignedTaskConfig) extends BridgeFourJob[F] {
    val jobClass: JobClass = JobClass.SampleJob

    override def run(): F[BackgroundTaskState] = Async[F].blocking(BackgroundTaskState(id = config.taskId.id, status = Done))
}