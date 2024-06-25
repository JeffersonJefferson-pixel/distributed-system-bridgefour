package com.example.bridgefour.shared.models

import org.latestbit.circe.adt.codec.JsonTaggedAdt

object Status {
  enum ExecutionStatus derives JsonTaggedAdt.Codec {
    case NotStarted
    case InProgress
    case Halted
    case Done
    case Error
    case Missing
  }
}