package com.example.bridgefour.shared.models

import com.example.bridgefour.shared.jobs.JobClass
import com.example.bridgefour.shared.models.IDs.{JobId, TaskId}
import com.example.bridgefour.shared.models.Status.ExecutionStatus
import io.circe.{Decoder, Encoder}

object Job {
  type DirPath = String
  type FilePath = String
  type JobName = String
  
  sealed trait JobConfig {
    def name: JobName
    def input: DirPath
    def output: DirPath
    def jobClass: JobClass
    def userSettings: Map[String, String]
  }
  // the input a user provides for a job
  case class UserJobConfig(
    name: JobName,
    jobClass: JobClass,
    input: DirPath,
    output: DirPath,
    userSettings: Map[String, String]                      
  ) extends JobConfig derives Encoder.AsObject, Decoder
  
  // the machine-generated JobConfig, with assigned tasks
  case class SystemJobConfig (
    id: JobId,
    name: JobName,
    jobClass: JobClass,
    input: DirPath,
    output: DirPath,
userSettings: Map[String, String]                         
  ) extends JobConfig
  
  object SystemJobConfig {
    def apply(id: JobId, cfg: UserJobConfig): SystemJobConfig =
      SystemJobConfig(id, cfg.name, cfg.jobClass, cfg.input, cfg.output, cfg.userSettings)
  }
  
  case class BackgroundTaskState(id: TaskId, status: ExecutionStatus) derives  Encoder.AsObject, Decoder
}
