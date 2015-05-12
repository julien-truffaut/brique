package brique.bench

import java.lang.Integer

import algebra.Order

import scala.Int

trait BenchInstances {

  implicit val intOrder = new Order[Int] {
    override def compare(x: Int, y: Int): Int = Integer.compare(x, y)
  }

}
