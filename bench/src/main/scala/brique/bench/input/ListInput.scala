package brique.bench.input

import org.openjdk.jmh.annotations.{Setup, Param, Scope, State}
import scala.{Array, Int, List, Unit}

@State(Scope.Thread)
class ListInput extends InputHelper {
  @Param(Array("10", "100", "1000"))
  var size: Int = _

  var list: List[Int] = _

  @Setup
  def setup(): Unit =
    list = List(genArray(size)(r.nextInt()): _*)
}
