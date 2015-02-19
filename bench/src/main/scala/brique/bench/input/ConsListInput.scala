package brique.bench.input

import brique.ConsList
import org.openjdk.jmh.annotations.{Setup, Param, Scope, State}

@State(Scope.Thread)
class ConsListInput extends InputHelper {
  @Param(Array("10", "100", "1000"))
  var size: Int = _

  var iList: ConsList[Int] = _

  @Setup
  def setup(): Unit =
    iList = ConsList(genArray(size)(r.nextInt()): _*)
}
