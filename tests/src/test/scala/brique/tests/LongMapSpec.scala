package brique.tests

import brique.LongMap
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

import java.lang.String

import scala.{Long, None, Some}
import scala.collection.immutable.Map
import scala.Predef.ArrowAssoc

class LongMapSpec extends FunSuite with Checkers {

  implicit def mapArb[K, V](implicit K: Arbitrary[K], V: Arbitrary[V]): Arbitrary[Map[K, V]] =
    Arbitrary(Gen.mapOf(Gen.zip(K.arbitrary, V.arbitrary)))

  implicit def longMapArb[A](implicit A: Arbitrary[A]): Arbitrary[LongMap[A]] = Arbitrary(
    Gen.listOf(Gen.zip(Arbitrary.arbLong.arbitrary, A.arbitrary)).map(kv => LongMap(kv: _*))
  )

  test("apply - toMap") {
    check((kv: Map[Long, String]) =>
      LongMap(kv.toSeq: _*).toMap === kv
    )
  }

  test("lookup1") {
    val lm = LongMap(1L -> "Hello", 56L -> "World", -25L -> "Test")
    assert(lm.lookup(56)  == Some("World"))
    assert(lm.lookup(-25) == Some("Test"))
    assert(lm.lookup(0)   == None)
    assert(lm.lookup(99)  == None)
  }

  test("lookup") {
    check { (kv: Map[Long, String]) =>
      val lm = LongMap(kv.toSeq: _*)
      kv.keySet.forall(key => lm.lookup(key) === kv.get(key))
    }
  }

  test("size") {
    check((kv: Map[Long, String]) =>
      LongMap(kv.toSeq: _*).size === kv.size
    )
  }



}
