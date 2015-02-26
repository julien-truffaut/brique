package brique.bench.input

import brique.ConsList
import org.openjdk.jmh.annotations.{Setup, Param, Scope, State}
import scala.{Array, Int, Unit}

@State(Scope.Thread)
class ConsListInput extends InputHelper {
  @Param(Array("10", "100", "1000"))
  var size: Int = _

  var iList: ConsList[Int] = _

  @Setup
  def setup(): Unit =
    iList = ConsList(range(size): _*)
}
