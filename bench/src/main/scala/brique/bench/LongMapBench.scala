package brique.bench

import java.util.concurrent.TimeUnit

import algebra.Eq
import brique.ConsList
import brique.bench.input._
import org.openjdk.jmh.annotations._
import scala.collection.immutable.Range
import scala.{Array, Boolean, Exception, Int, List, Option}

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class LongMapBench {

  @Benchmark def lookupLongMap(in: LongMapInput): Option[Int] =
    in.longMap.lookup(in.validIndex)

  @Benchmark def lookupStdLongMap(in: StdLongMapInput): Option[Int] =
    in.longMap.get(in.validIndex)


}
