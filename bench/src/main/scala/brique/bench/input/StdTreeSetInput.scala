package brique.bench.input

import org.openjdk.jmh.annotations.{Param, Scope, Setup, State}

import scala.util.Random
import scala.{Array, Int, List, Long, Unit}
import scala.collection.immutable.TreeSet

@State(Scope.Thread)
class StdTreeSetInput extends InputHelper {
  @Param(Array("10", "100", "1000", "10000"))
  var size: Int = _

  var treeSet: TreeSet[Int] = _

  @Setup
  def setup(): Unit = {
    treeSet = TreeSet(genArray(size)(r.nextInt): _*)
  }

}
