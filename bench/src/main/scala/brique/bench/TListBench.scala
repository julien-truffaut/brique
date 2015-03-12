package brique.bench

import java.util.concurrent.TimeUnit

import brique.TList
import brique.bench.input.{ListInput, TListInput}
import org.openjdk.jmh.annotations._
import scala.{Array, Int, List}


@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class TListBench {

  @Benchmark def reverseTList(in: TListInput): TList[Int] =
    in.tList.reverse

  @Benchmark def reverseList(in: ListInput): List[Int] =
    in.list.reverse

}
