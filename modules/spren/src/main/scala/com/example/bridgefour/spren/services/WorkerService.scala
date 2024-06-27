package com.example.bridgefour.spren.services

import cats.Parallel
import cats.effect.Sync
import com.example.bridgefour.shared.models.States.SlotState
import com.example.bridgefour.shared.models.Worker.WorkerState
import org.typelevel.log4cats.Logger
import com.example.bridgefour.shared.models.Config.SprenConfig
import cats.implicits.*
import com.example.bridgefour.spren.programs.TaskExecutor

sealed trait WorkerService[F[_]] {
  def state(): F[WorkerState]
}

object WorkerService {
  def make[F[_]: Parallel: Sync: Logger](
    cfg: SprenConfig,
    executor: TaskExecutor[F]
  ): WorkerService[F] = new WorkerService[F] {
    private def slots(): F[List[SlotState]] = {
      for {
        res <- Range(0, cfg.maxSlots).toList.parTraverse { i =>
          executor.getSlotState(i)
        }
        _ <- Logger[F].debug(s"Slot scan returned: $res")
      } yield res
    }

    override def state(): F[WorkerState] = for {
      slots <- slots()
    } yield WorkerState(cfg.id, slots)
  }
}
