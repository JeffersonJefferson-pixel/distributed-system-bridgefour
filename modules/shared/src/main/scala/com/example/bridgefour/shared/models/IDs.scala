package com.example.bridgefour.shared.models

import io.circe.{Decoder, Encoder}

object IDs {
  type JobId = Int
  type TaskId = Int
  type WorkerId = Int
  type SlotId = Int
  case class TaskIdTuple(id: TaskId, jobId: JobId) derives Encoder.AsObject, Decoder
  case class SlotIdTuple(id: SlotId, workerId: WorkerId) derives Encoder.AsObject, Decoder
}
