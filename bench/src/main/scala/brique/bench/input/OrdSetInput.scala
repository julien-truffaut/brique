package brique.bench.input

import brique.OrdSet
import brique.bench.BenchInstances
import org.openjdk.jmh.annotations.{Param, Scope, Setup, State}

import scala.util.Random
import scala.{Array, Int, List, Unit}

@State(Scope.Thread)
class OrdSetInput extends InputHelper with BenchInstances {
  @Param(Array("10", "100", "1000", "10000"))
  var size: Int = _

  var ordSet: OrdSet[Int] = _

  @Setup
  def setup(): Unit =
    ordSet = OrdSet(genArray(size)(r.nextInt): _*)
}
