package com.example.bridgefour.shared.background

import cats.effect.Fiber
import com.example.bridgefour.shared.background.BackgroundWorker.{BackgroundWorkerResult, FiberContainer}
import com.example.bridgefour.shared.models.Status.ExecutionStatus

import scala.concurrent.duration.FiniteDuration


trait BackgroundWorker[F[_], A, M] {
  def start(key: Long, f: F[A], meta: Option[M] = None): F[ExecutionStatus]
  def get(key: Long): F[Option[FiberContainer[F, A, M]]]
  def getResult(key: Long): F[BackgroundWorkerResult[F, A, M]]
  def probeResult(key: Long, timeout: FiniteDuration): F[BackgroundWorkerResult[F, A, M]]
}

object BackgroundWorker {
  case class FiberContainer[F[_], A, M](fib: Fiber[F, Throwable, A], meta: Option[M])
  case class BackgroundWorkerResult[F[_], A, M](res: Either[ExecutionStatus, A], meta: Option[M])
}