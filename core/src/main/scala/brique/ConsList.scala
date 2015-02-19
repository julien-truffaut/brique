package brique

import algebra.{Eq, Monoid}
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
 * Purely functional single linked list
 * ConsList is strongly inspired by scalaz.IList
 */
sealed abstract class ConsList[A] extends Product with Serializable {
  import brique.ConsList._

  /** add an element to the back */
  final def append(a: A): ConsList[A] =
    reverse.foldLeft(ConsList.singleton(a))((acc, a) => Cons(a, acc))

  /** add an [[ConsList]] to the back */
  final def concat(as: ConsList[A]): ConsList[A] =
    reverse.foldLeft(as)((acc, a) => Cons(a, acc))

  /** alias for concat */
  final def ++(as: ConsList[A]): ConsList[A] =
    reverse.foldLeft(as)((acc, a) => Cons(a, acc))

  /** drop the `n` first elements */
  @tailrec final def drop(n: Int): ConsList[A] = this match {
    case CNil()    => this
    case Cons(h,t) => if(n > 0) t.drop(n - 1) else this
  }

  /** drop elements as long as the predicate holds */
  @tailrec final def dropWhile(p: A => Boolean): ConsList[A] = this match {
    case CNil()    => this
    case Cons(h,t) => if(p(h)) t.dropWhile(p) else this
  }

  /** filter all elements that match the predicate */
  final def filter(p: A => Boolean): ConsList[A] = {
    @tailrec
    def loop(as: ConsList[A], acc: ConsList[A]): ConsList[A] = as match {
      case CNil()     => acc.reverse
      case Cons(h, t) => if(p(h)) loop(t, Cons(h, acc)) else loop(t, acc)
    }
    loop(this, empty[A])
  }

  final def flatMap[B](f: A => ConsList[B]): ConsList[B] =
    reverse.foldLeft(empty[B])((acc, a) => f(a) ++ acc )

  @tailrec
  final def foldLeft[B](b: B)(f: (B, A) => B): B = this match {
    case CNil()     => b
    case Cons(h, t) => t.foldLeft(f(b,h))(f)
  }

  final def foldMap[B](b: B)(f: A => B)(implicit B: Monoid[B]): B =
    reverse.foldLeft(b)((acc, a) => B.combine(f(a), acc))

  final def foldRight[B](b: B)(f: (A, B) => B): B =
    reverse.foldLeft(b)((b, a) => f(a, b))

  /** get the head if the [[ConsList]] is not empty */
  final def headOption: Option[A] = this match {
    case CNil()     => Option.empty
    case Cons(h, _) => Some(h)
  }

  /** check if an [[ConsList]] is empty */
  final def isEmpty: Boolean = this match {
    case CNil()     => true
    case Cons(_, _) => false
  }

  /** get the last element if the [[ConsList]] is not empty */
  final def lastOption: Option[A] = {
    @tailrec def loop(as: ConsList[A]): A = (as: @unchecked) match {
      case Cons(h, CNil()) => h
      case Cons(_, t)      => loop(t)
    }
    if(isEmpty) None
    else Some(loop(this))
  }

  /** get the element at the index if it exists */
  final def lookup(index: Int): Option[A] = {
    @tailrec
    def loop(as: ConsList[A], i: Int): Option[A] = as match {
      case Cons(h, t) =>
        if(i > 0) loop(t, i - 1)
        else if(i == 0) Some(h)
        else None
      case CNil() => None
    }
    loop(this, index)
  }

  final def map[B](f: A => B): ConsList[B] =
    reverse.foldLeft(empty[B])((acc, a) => Cons(f(a), acc))

  /** add an element to the front */
  final def prepend(a: A): ConsList[A] =
    Cons(a, this)

  /** alias for prepend */
  final def ::(a: A): ConsList[A] =
    Cons(a, this)

  /** reverse an [[ConsList]] */
  final def reverse: ConsList[A] =
    foldLeft(empty[A])((acc, a) => Cons(a, acc))

  /** compute the size of an [[ConsList]] */
  final def size: Int = {
    @inline @tailrec def loop(as: ConsList[A], acc: Int): Int = as match {
      case Cons(_, t) => loop(t, acc + 1)
      case CNil()     => acc
    }
    loop(this, 0)
  }

  /** get the tail if the [[ConsList]] is not empty */
  final def tailOption: Option[ConsList[A]] = this match {
    case CNil()     => Option.empty
    case Cons(_, t) => Some(t)
  }

  /** take the `n` first elements */
  final def take(n: Int): ConsList[A] = {
    @tailrec
    def loop(as: ConsList[A], m: Int, acc: ConsList[A]): ConsList[A] = as match {
      case CNil()    => acc.reverse
      case Cons(h,t) => if(m > 0) loop(t, m - 1, Cons(h, acc)) else acc.reverse
    }
    loop(this, n, empty)
  }

  /** take elements as long as the predicate holds */
  final def takeWhile(p: A => Boolean): ConsList[A] = {
    @tailrec
    def loop(as: ConsList[A], acc: ConsList[A]): ConsList[A] = as match {
      case CNil()    => acc.reverse
      case Cons(h,t) => if(p(h)) loop(t, Cons(h, acc)) else acc.reverse
    }
    loop(this, empty)
  }

  /** transform an [[ConsList]] into a [[scala.List]] */
  final def toList: List[A] =
    foldLeft(ListBuffer.empty[A])(_ += _).toList

  /** attempt to get head and tail of an [[ConsList]] */
  final def uncons: Option[(A, ConsList[A])] = this match {
    case CNil()     => Option.empty
    case Cons(h, t) => Some((h,t))
  }

  /** widen the type of an [[ConsList]] */
  final def widen[B >: A]: ConsList[B] =
    this.asInstanceOf[ConsList[B]]

  /** check if two matches are equal */
  final def ===(other: ConsList[A])(implicit A: Eq[A]): Boolean = {
    @inline @tailrec
    def loop(as: ConsList[A], bs: ConsList[A]): Boolean = (as, bs) match {
      case (CNil(), CNil()) => true
      case (Cons(x, xs), Cons(y, ys)) => A.eqv(x,y) && loop(xs, ys)
      case _ => false
    }
    loop(this, other)
  }

}

object ConsList extends ConsListInstances {
  final case class CNil[A]() extends ConsList[A]
  final case class Cons[A](head: A, tail: ConsList[A]) extends ConsList[A]

  private val nil: ConsList[Nothing] = CNil()

  /** create an [[ConsList]] with a single element */
  def singleton[A](a: A): ConsList[A] =
    Cons(a, empty)

  /** create an empty [[ConsList]] */
  def empty[A]: ConsList[A] =
    nil.asInstanceOf[ConsList[A]]

  /** create an [[ConsList]] from a varargs */
  def apply[A](as: A*): ConsList[A] =
    as.foldRight(empty[A])(Cons(_,_))

}

sealed abstract class ConsListInstances {
  implicit def ilistEq[A: Eq]: Eq[ConsList[A]] = new Eq[ConsList[A]]{
    def eqv(x: ConsList[A], y: ConsList[A]): Boolean =
      x === y
  }

  implicit def ilistMonoid[A]: Monoid[ConsList[A]] = new Monoid[ConsList[A]] {
    def empty: ConsList[A] =
      ConsList.empty

    def combine(x: ConsList[A], y: ConsList[A]): ConsList[A] =
      x concat y
  }
}
