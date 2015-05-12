package brique.tests

import algebra.{Order, Eq, Monoid}
import brique.{OrdSet, ConsList, LongMap}
import org.scalacheck.{Arbitrary, Gen}

import java.lang.{Integer, String}

import scala.collection.immutable.Map
import scala.{Boolean, Int, Long, Option, None, Some}

trait TestInstances {

  /** Algebra instances */

  implicit val intOrder = new Order[Int] {
    override def compare(x: Int, y: Int): Int = Integer.compare(x, y)
  }

  implicit val longOrder = new Order[Long] {
    override def compare(x: Long, y: Long): Int = java.lang.Long.compare(x, y)
  }

  implicit def optionEq[A](implicit A: Eq[A]): Eq[Option[A]] = new Eq[Option[A]] {
    def eqv(x: Option[A], y: Option[A]): Boolean = (x, y) match {
      case (None, None) => true
      case (Some(a1), Some(a2)) => A.eqv(a1, a2)
      case _ => false
    }
  }


  implicit val stringMonoid = new Monoid[String] {
    def empty: String = ""
    def combine(x: String, y: String): String = x + y
  }

  /**  Arbitrary instances */

  implicit def consListArb[A](implicit A: Arbitrary[A]): Arbitrary[ConsList[A]] = Arbitrary(
    Gen.listOf(A.arbitrary).map(l => ConsList(l: _*))
  )

  implicit def mapArb[K, V](implicit K: Arbitrary[K], V: Arbitrary[V]): Arbitrary[Map[K, V]] =
    Arbitrary(Gen.mapOf(Gen.zip(K.arbitrary, V.arbitrary)))

  implicit def longMapArb[A](implicit A: Arbitrary[A]): Arbitrary[LongMap[A]] = Arbitrary(
    Gen.listOf(Gen.zip(Arbitrary.arbLong.arbitrary, A.arbitrary)).map(kv => LongMap(kv: _*))
  )

  implicit def ordSetArb[A](implicit arb: Arbitrary[A], ord: Order[A]): Arbitrary[OrdSet[A]] = Arbitrary(
    Gen.listOf(arb.arbitrary).map(l => OrdSet(l: _*))
  )

}
