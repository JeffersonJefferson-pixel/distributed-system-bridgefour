package com.example.bridgefour.shared.persistence

import cats.effect.Sync
import cats.effect.std.MapRef
import cats.implicits.toFunctorOps


sealed trait Persistence[F[_], K, V] {
  def put(key: K, value: V): F[Option[V]]
  def get(key: K): F[Option[V]]
  def del(key: K): F[Option[V]]
}

object InMemoryPersistence {
  def makeF[F[_] : Sync, K, V](): F[Persistence[F, K, V]] = {
    for {
      storage <- MapRef.ofScalaConcurrentTrieMap[F, K, V]
    } yield new Persistence[F, K, V]() {
      def put(key: K, value: V): F[Option[V]] = storage(key).getAndSet(Some(value))

      def get(key: K): F[Option[V]] = storage(key).get

      def del(key: K): F[Option[V]] = storage(key).getAndSet(None)
    }
  }
}