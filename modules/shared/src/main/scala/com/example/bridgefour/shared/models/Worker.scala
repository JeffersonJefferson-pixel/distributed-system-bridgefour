package com.example.bridgefour.shared.models

import com.example.bridgefour.shared.models.IDs.{SlotId, TaskIdTuple, WorkerId}
import com.example.bridgefour.shared.models.States.SlotState
import io.circe.{Decoder, Encoder}
import org.latestbit.circe.adt.codec.JsonTaggedAdt

enum WorkerStatus derives JsonTaggedAdt.Codec {
  case Alive
  case Dead
}

object Worker {
  case class WorkerState private (
    id: WorkerId,
    slots: List[SlotState],
    status: WorkerStatus = WorkerStatus.Alive
  ) derives Encoder.AsObject, Decoder {
    val allSlotIds: List[SlotId] = slots.map(_.id)
    val availableSlots: List[SlotId] = slots.filter(_.available()).map(_.id)
  }

  object WorkerState {
    def apply(id: WorkerId, slots: List[SlotState]): WorkerState = {
      val allSlotIds = slots.map(_.id)
      val status = if (slots.isEmpty) WorkerStatus.Dead else WorkerStatus.Alive
      new WorkerState(id, slots, status)
    }
  }
}
