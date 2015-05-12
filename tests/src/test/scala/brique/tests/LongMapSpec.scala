package brique.tests

import brique.LongMap

import java.lang.String

import scala.{Long, None, Some}
import scala.collection.immutable.Map
import scala.Predef.ArrowAssoc

class LongMapSpec extends BriqueSuite {

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
