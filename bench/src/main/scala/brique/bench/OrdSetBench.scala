package brique.bench

import java.util.concurrent.TimeUnit

import brique.OrdSet
import brique.bench.input.{StdTreeSetInput, IndexInput, OrdSetInput}
import org.openjdk.jmh.annotations._

import scala.collection.immutable.TreeSet
import scala.{Array, Boolean, Int}

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class OrdSetBench extends BenchInstances {

  @Benchmark def insert(ordIn: OrdSetInput, intIn: IndexInput): OrdSet[Int] =
    ordIn.ordSet.insert(intIn.index)

  @Benchmark def stdInsert(ordIn: StdTreeSetInput, intIn: IndexInput): TreeSet[Int] =
    ordIn.treeSet.insert(intIn.index)

  @Benchmark def contains(ordIn: OrdSetInput): Boolean =
    ordIn.ordSet.contains(0)

  @Benchmark def stdContains(ordIn: StdTreeSetInput): Boolean =
    ordIn.treeSet.contains(0)

}
