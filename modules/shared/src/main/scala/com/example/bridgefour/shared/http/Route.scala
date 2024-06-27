package com.example.bridgefour.shared.http

import org.http4s.HttpRoutes

trait Route[F[_]] {
  protected def prefixPath: String
  protected def httpRoutes(): HttpRoutes[F]
  def routes: HttpRoutes[F]
}
