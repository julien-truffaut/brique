package brique.bench.input

import scala.collection.immutable.Range
import scala.reflect.ClassTag
import scala.util.Random
import scala.{Array, Int}

trait InputHelper {

  val r: Random = new Random()

  def genArray[A: ClassTag](size:Int)(f: => A): Array[A] = {
    val data = Array.ofDim[A](size)
    for (i <- Range(0, size)) data(i) = f
    data
  }

  def range(size: Int): Range =
    Range.inclusive(0, size)

}
