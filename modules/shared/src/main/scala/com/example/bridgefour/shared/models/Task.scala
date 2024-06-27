package com.example.bridgefour.shared.models

import com.example.bridgefour.shared.jobs.JobClass
import com.example.bridgefour.shared.models.IDs.{SlotIdTuple, TaskIdTuple}
import com.example.bridgefour.shared.models.Job.{DirPath, FilePath, SystemJobConfig}
import io.circe.{Decoder, Encoder}

object Task {
  sealed trait TaskConfig derives Encoder.AsObject, Decoder {
    def jobClass: JobClass
    def input: FilePath
    def output: DirPath
    def userSettings: Map[String, String]
  }

  // instruct a worker on a task for a given job
  case class AssignedTaskConfig(
    taskId: TaskIdTuple,
    slotId: SlotIdTuple,
    input: FilePath,
    output: DirPath,
    jobClass: JobClass,
    userSettings: Map[String, String]
  ) extends TaskConfig derives Encoder.AsObject, Decoder {
    val outputFile: FilePath = s"$output/part-${taskId.id}-${taskId.jobId}.txt"
  }

  object AssignedTaskConfig {
    def apply(
      taskId: TaskIdTuple,
      slotId: SlotIdTuple,
      input: FilePath,
      job: SystemJobConfig,
      userSettings: Map[String, String]
    ): AssignedTaskConfig = AssignedTaskConfig(
      taskId = taskId,
      slotId = slotId,
      input = input,
      output = job.output,
      jobClass = job.jobClass,
      userSettings = userSettings
    )
  }
}
