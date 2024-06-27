package com.example.bridgefour.shared.models

import com.example.bridgefour.shared.models.IDs.{JobId, SlotId, TaskId, TaskIdTuple, WorkerId}
import com.example.bridgefour.shared.models.Status.ExecutionStatus
import io.circe.{Decoder, Encoder}

object States {
  // A SlotState is reported by a worker. They are unaware of what exactly they are working on. The state is ephemeral.
  // The leader keeps track of it persistently.
  case class SlotState private (
    id: SlotId,
    status: ExecutionStatus
  ) derives Encoder.AsObject, Decoder {
    def available(): Boolean = ExecutionStatus.available(status)
  }

  object SlotState {
    def apply(id: SlotId, status: ExecutionStatus): SlotState = new SlotState(id, status = status)

    def started(id: SlotId, taskId: TaskIdTuple): SlotState = SlotState(id, ExecutionStatus.InProgress)

    def empty(id: SlotId): SlotState = SlotState(id, ExecutionStatus.Missing)
  }
  
  // a task state is a database object that only exists for the leader to keep track of state of a task 
  case class TaskState(
    id: TaskId, 
    jobId: JobId, 
    workerId: WorkerId, 
    status: ExecutionStatus
  ) derives Encoder.AsObject, Decoder
  
}
