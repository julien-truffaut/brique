package brique

import brique.ConsList.{CNil, Cons}
import brique.LongMap.internal._
import brique.internal.BitsUtil

import scala.{Boolean, Int, Long, Nothing, Option, None, Some}
import scala.collection.immutable.Map


sealed abstract class LongMap[A] extends scala.Product with scala.Serializable {

  final def isEmpty: Boolean = this.isInstanceOf[LMEmpty[A]]

  final def foldLeft[B](b: B)(f: (B, A) => B): B = {
    var ms: ConsList[LongMap[A]] = ConsList.singleton(this)
    var acc = b
    while(true){
      ms match {
        case Cons(Bin(_, _, l, r), t) => ms = l :: r :: t
        case Cons(Tip(_, a), t)       => acc = f(acc, a); ms = t
        case Cons(LMEmpty(), t)       => ms = t
        case CNil()                   => return acc
      }
    }
    acc
  }

  final def foldLeftWithKey[B](b: B)(f: (B, Long, A) => B): B = {
    var ms: ConsList[LongMap[A]] = ConsList.singleton(this)
    var acc = b
    while(true){
      ms match {
        case Cons(Bin(_, _, l, r), t) => ms = l :: r :: t
        case Cons(Tip(k, a), t)       => acc = f(acc, k, a); ms = t
        case Cons(LMEmpty(), t)       => ms = t
        case CNil()                   => return acc
      }
    }
    acc
  }

  final def size: Int =
    foldLeft(0)((n, _) => n + 1)

  final def lookup(key: Long): Option[A] = {
    var lm: LongMap[A] = this
    while(true){
      lm match {
        case Bin(p, m, l, r) => lm = if(BitsUtil.zero(key, m)) l else r
        case Tip(k, a)       => return if(k == key) Some(a) else None
        case LMEmpty()       => return None
      }
    }
    None
  }

  final def insert(key: Long, value: A): LongMap[A] = this match {
    case LMEmpty() => Tip(key, value)
    case t@Tip(k, _) =>
      if(k == key) t.copy(value = value)
      else link(key, Tip(key, value), k, t)
    case b@Bin(p, m, l, r) =>
      if(BitsUtil.noMatch(key, p, m)) link(key, Tip(key, value), p, b)
      else if(BitsUtil.zero(key, m)) Bin(p, m, l.insert(key, value), r)
      else Bin(p, m, l, r.insert(key, value))
  }

  final def values: ConsList[A] =
    foldLeft(ConsList.empty[A])((acc, a) => a :: acc)

  final def toMap: Map[Long, A] =
    foldLeftWithKey(Map.empty[Long, A])((acc, k, v) => acc.+((k, v)))

}

object LongMap {

  private val lmEmpty: LongMap[Nothing] = LMEmpty()

  def apply[A](pairs: (Long, A)*): LongMap[A] =
    pairs.foldLeft(empty[A]){ case (acc, (k, v)) => acc.insert(k, v) }

  def empty[A]: LongMap[A] = lmEmpty.asInstanceOf[LongMap[A]]

  def singleton[A](key: Long, value: A): LongMap[A] = Tip(key, value)

  object internal {
    final case class Bin[A](prefix: Long, mask: Long, left: LongMap[A], right: LongMap[A]) extends LongMap[A]
    final case class Tip[A](key: Long, value: A) extends LongMap[A]
    final case class LMEmpty[A]() extends LongMap[A]

    def link[A](p1: Long, l1: LongMap[A], p2: Long, l2: LongMap[A]): LongMap[A] = {
      val m = BitsUtil.branchMask(p1, p2)
      val p = BitsUtil.mask(p1, m)
      if(BitsUtil.zero(p1, m))
        Bin(p, m, l1, l2)
      else
        Bin(p, m, l2, l1)
    }

  }




}