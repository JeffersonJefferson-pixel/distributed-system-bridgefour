package com.example.bridgefour.spren.models

import com.example.bridgefour.shared.models.Config.SprenConfig
import cats.effect.Sync
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.derivation.default.*
import pureconfig.module.catseffect.syntax.*

object Config {
  case class ServiceConfig(
    self: SprenConfig,
    leader: LeaderConfig
  ) derives ConfigReader

  case class LeaderConfig(
    host: String,
    port: Int = 5555
  ) derives ConfigReader

  def load[F[_]: Sync](): F[ServiceConfig] = ConfigSource.default.loadF[F, ServiceConfig]()
}
