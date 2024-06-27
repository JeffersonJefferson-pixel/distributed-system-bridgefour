package com.example.bridgefour.shared.persistence

import cats.effect.IO
import cats.implicits.*
import cats.effect.std.Random
import com.example.bridgefour.shared.persistence.{Counter, InMemoryCounter}
import munit.CatsEffectSuite

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class CounterSuite extends CatsEffectSuite {
  test("InMemoryCountry works across threads") {
    Random
      .scalaUtilRandom[IO]
      .map { ev =>
        Range(0, 11).toList.traverse { _ =>
          def countT(ctr: Counter[IO, Int], i: Int, key: Int = 0): IO[Unit] = for {
            r <- Random[IO](ev).betweenInt(1, 25)
            _ <- IO.sleep(FiniteDuration(r, TimeUnit.MILLISECONDS))
            _ <- if (i == 1) ctr.inc(key) else ctr.dec(key)
            r <- Random[IO](ev).betweenInt(1, 25)
            _ <- IO.sleep(FiniteDuration(r, TimeUnit.MILLISECONDS))
          } yield ()
          for {
            ctr <- InMemoryCounter.makeF[IO]()
            f1 <- Range(0, 1001).toList.map(_ => countT(ctr, 1, 0).start).sequence
            f2 <- countT(ctr, -1).start
            _ <- f1.traverse(_.join)
            _ <- f2.join
            r <- ctr.get(0)
            _ = assertEquals(r, 1000L)
          } yield()
        }
      }
      .flatten
  }
}
