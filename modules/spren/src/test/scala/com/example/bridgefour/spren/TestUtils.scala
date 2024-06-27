package com.example.bridgefour.spren

import cats.effect.IO
import com.example.bridgefour.shared.jobs.{BridgeFourJob, JobClass, JobCreator}
import com.example.bridgefour.shared.models.Config.SprenConfig
import com.example.bridgefour.shared.models.IDs.{SlotIdTuple, TaskIdTuple}
import com.example.bridgefour.shared.models.Job.BackgroundTaskState
import com.example.bridgefour.shared.models.Status.ExecutionStatus.Done
import com.example.bridgefour.shared.models.Task.AssignedTaskConfig

import scala.concurrent.duration.DurationDouble
import scala.language.postfixOps

object TestUtils {
  val sprenCfg = SprenConfig(0, "http", "0.0.0.0", 5555, 2, 0.2 seconds)

  val jobId = 100
  val taskId = 200
  val workerId = 0
  val slotId = 0
  val taskIdTuple = TaskIdTuple(taskId, jobId)
  val slotIdTuple = SlotIdTuple(slotId, workerId)

  val sampleTask: AssignedTaskConfig = AssignedTaskConfig(
    taskId = taskIdTuple,
    slotId = slotIdTuple,
    input = "sample",
    output = "out",
    jobClass = JobClass.SampleJob,
    userSettings = Map("taskId" -> taskId.toString)
  )

  object Jobs {
    case class AlwaysOkBridgeFourJob(config: AssignedTaskConfig) extends BridgeFourJob[IO] {
      val jobClass: JobClass = JobClass.AlwaysOkJob

      def run(): IO[BackgroundTaskState] = IO.println("starting") >>
        IO.pure(
          BackgroundTaskState(id = config.userSettings("taskId").toInt, status = Done)
        ) <* IO.println("Done")
    }



    case class FakeJobCreator() extends JobCreator[IO] {
      def makeJob(cfg: AssignedTaskConfig) = AlwaysOkBridgeFourJob(cfg)
    }
  }
}
