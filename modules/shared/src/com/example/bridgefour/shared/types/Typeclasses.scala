package com.example.bridgefour.shared.types

import cats.MonadError

object Typeclasses {
  type ThrowableMonadError[F[_]] = MonadError[F, Throwable]
}
