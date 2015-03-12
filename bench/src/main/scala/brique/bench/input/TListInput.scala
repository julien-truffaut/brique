package brique.bench.input

import brique.TList
import brique.TList.{TCons, TNil}
import org.openjdk.jmh.annotations.{Setup, Param, Scope, State}
import scala.{Array, Int, Unit}

@State(Scope.Thread)
class TListInput extends InputHelper {
  @Param(Array("10", "100", "1000"))
  var size: Int = _

  var tList: TList[Int] = _

  @Setup
  def setup(): Unit =
    tList = range(size).foldRight[TList[Int]](TNil())(TCons(_, _))
}

