package brique.bench.input

import org.openjdk.jmh.annotations.{Param, Scope, Setup, State}

import scala.{Array, Int, List, Long, Unit}
import scala.collection.immutable.LongMap

@State(Scope.Thread)
class StdLongMapInput extends InputHelper {
   @Param(Array("10", "100", "1000", "10000"))
   var size: Int = _

   var longMap: LongMap[Int] = _

   var validIndex: Long = _

   @Setup
   def setup(): Unit = {
     longMap = LongMap(range(size).map(_.toLong).map((_,r.nextInt())): _*)
     validIndex= r.nextInt(size).toLong + 500
   }

 }