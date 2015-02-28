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
class ConsListBench {

  implicit val intEq = new Eq[Int] {
    def eqv(x: Int, y: Int): Boolean = x == y
  }

  @Benchmark def sumConsList(in: ConsListInput): Int =
    in.consList.foldLeft(0)(_ + _)

  @Benchmark def sumList(in: ListInput): Int =
    in.list.sum

  @Benchmark def appendConsList(in: ConsListInput): ConsList[Int] =
    in.consList.append(5)

  @Benchmark def appendList(in: ListInput): List[Int] =
    in.list :+ 5

  @Benchmark def mapConsList(in: ConsListInput): ConsList[Int] =
    in.consList.map(_ + 1)

  @Benchmark def mapList(in: ListInput): List[Int] =
    in.list.map(_ + 1)

  @Benchmark def takeConsList(in: ConsListInput, i: IndexInput): ConsList[Int] =
    in.consList.take(i.index)

  @Benchmark def takeList(in: ListInput, i: IndexInput): List[Int] =
    in.list.take(i.index)

  @Benchmark def dropConsList(in: ConsListInput, i: IndexInput): ConsList[Int] =
    in.consList.drop(i.index)

  @Benchmark def dropList(in: ListInput, i: IndexInput): List[Int] =
    in.list.drop(i.index)

  @Benchmark def lookupConsList(in: ConsListInput, i: IndexInput): Option[Int] =
    in.consList.lookup(i.index)

  @Benchmark def lookUpList(in: ListInput, i: IndexInput): Option[Int] =
    try {
      Option(in.list.apply(i.index))
    } catch {
      case _: Exception => Option.empty
    }

  @Benchmark def lastOptionConsList(in: ConsListInput): Option[Int] =
    in.consList.lastOption

  @Benchmark def lastOptionList(in: ListInput): Option[Int] =
    in.list.lastOption

  @Benchmark def concatConsList(in1: ConsListInput, in2: ConsListInput): ConsList[Int] =
    in1.consList ++ in2.consList

  @Benchmark def concatList(in1: ListInput, in2: ListInput): List[Int] =
    in1.list ++ in2.list

  @Benchmark def flatMapConsList(in: ConsListInput): ConsList[Int] =
    in.consList.flatMap(i => ConsList(i - 1, i, i + 1))

  @Benchmark def flatMapList(in: ListInput): List[Int] =
    in.list.flatMap(i => List(i - 1, i, i + 1))

  @Benchmark def filterConsList(in: ConsListInput): ConsList[Int] =
    in.consList.filter(_ % 2 == 0)

  @Benchmark def filterList(in: ListInput): List[Int] =
    in.list.filter(_ % 2 == 0)

  @Benchmark def applyConsList(in: ArrayInput): ConsList[Int] =
    ConsList(in.array: _*)

  @Benchmark def applyList(in: ArrayInput): List[Int] =
    List(in.array: _*)

  @Benchmark def eqConsList(in1: ConsListInput, in2: ConsListInput): Boolean =
    in1.consList === in2.consList

  @Benchmark def eqList(in1: ListInput, in2: ListInput): Boolean =
    in1.list.equals(in2.list)

  @Benchmark def sizeConsList(in: ConsListInput): Int =
    in.consList.size

  @Benchmark def sizeList(in: ListInput): Int =
    in.list.size

  @Benchmark def reverseConsList(in: ConsListInput): ConsList[Int] =
    in.consList.reverse

  @Benchmark def reverseList(in: ListInput): List[Int] =
    in.list.reverse

}
