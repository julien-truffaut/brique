package brique

import algebra.{Order, Eq, Monoid}
import cats.Fold.{Continue, Return}
import cats._
import java.lang.String
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.{Boolean, Int, List, None, Nothing, Option, Some}
import scala.{inline, unchecked}

/**
 * Purely functional single linked list
 * [[ConsList]] is inspired by scalaz.IList
 */
sealed abstract class ConsList[A] extends scala.Product with scala.Serializable {
  import brique.ConsList._

  /** add an element to the back */
  final def append(a: A): ConsList[A] =
    reverse.foldLeft(ConsList.singleton(a))((acc, a) => Cons(a, acc))

  /** add a [[ConsList]] to the back */
  final def concat(as: ConsList[A]): ConsList[A] =
    reverse.foldLeft(as)((acc, a) => Cons(a, acc))

  /** alias for concat */
  final def ++(as: ConsList[A]): ConsList[A] =
    reverse.foldLeft(as)((acc, a) => Cons(a, acc))

  /** drop the `n` first elements */
  final def drop(n: Int): ConsList[A] = {
    var acc = this
    var m = n
    while(true){
      acc match {
        case CNil() => return acc
        case Cons(h, t) =>
          if(m > 0) { acc = t; m = m - 1 }
          else return acc
      }
    }
    acc
  }

  /** drop elements as long as the predicate holds */
  final def dropWhile(p: A => Boolean): ConsList[A] = {
    var acc = this
    while(true){
      acc match {
        case CNil() => return acc
        case Cons(h, t) =>
          if(p(h)) acc = t
          else return acc
      }
    }
    acc
  }

  /** filter all elements that match the predicate */
  final def filter(p: A => Boolean): ConsList[A] = {
    var acc = empty[A]
    var l = this
    while(true){
      l match {
        case Cons(h, t) =>
          if(p(h)) acc = Cons(h, acc)
          l = t
        case CNil() => return acc.reverse
      }
    }
    acc
  }

  final def flatMap[B](f: A => ConsList[B]): ConsList[B] =
    reverse.foldLeft(empty[B])((acc, a) => f(a) ++ acc )

  final def foldLeft[B](b: B)(f: (B, A) => B): B = {
    var acc = b
    var l = this
    while(true){
      l match {
        case Cons(h, t) =>
          acc = f(acc, h)
          l = t
        case CNil() => return acc
      }
    }
    acc
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

  /** check if a [[ConsList]] is empty */
  final def isEmpty: Boolean = this match {
    case CNil()     => true
    case Cons(_, _) => false
  }

  final def isSorted(implicit A: Order[A]): Boolean = this match {
    case CNil()          => true
    case Cons(_, CNil()) => true
    case Cons(first, as) =>
      var previous = first
      var l = as
      while(true){
        l match {
          case CNil()     => return true
          case Cons(h, t) =>
            if(A.lteqv(previous, h)){
              previous = h
              l = t
            } else return false
        }
      }
      true
  }

  /** get the last element if the [[ConsList]] is not empty */
  final def lastOption: Option[A] = {
    this match {
      case CNil() => None
      case Cons(head, tail) =>
        var last = head
        var l = tail
        while(true){
          l match {
            case Cons(h, t) =>
              last = h
              l = t
            case CNil() => return Some(last)
          }
        }
        Some(last)
    }
  }

  /** get the element at the index if it exists */
  final def lookup(index: Int): Option[A] = {
    var l = this
    var i = index
    while(true){
      l match {
        case Cons(h, t) =>
          if(i > 0){ i = i - 1; l = t }
          else if(i == 0) return Some(h)
          else return None
        case CNil() => return None
      }
    }
    None
  }

  final def map[B](f: A => B): ConsList[B] = {
    var acc = empty[B]
    var l = reverse
    while(true){
      l match {
        case Cons(h, t) => acc = Cons(f(h), acc); l = t
        case CNil() => return acc
      }
    }
    acc
  }

  def partialFold[B](f: A => Fold[B]): Fold[B] = {
    def unroll(b: B, fs: ConsList[B => B]): B =
      fs.foldLeft(b)((b, f) => f(b))
    @tailrec
    def loop(current: ConsList[A], fs: ConsList[B => B]): Fold[B] =
      current match {
        case CNil()     => Continue(b => unroll(b, fs))
        case Cons(h, t) => f(h) match {
          case Return(b)   => Return(unroll(b, fs))
          case Continue(f) => loop(t, f :: fs)
          case _           => loop(t, fs)
        }
      }
    loop(this, CNil())
  }

  /** add an element to the front */
  final def prepend(a: A): ConsList[A] =
    Cons(a, this)

  /** alias for prepend */
  final def ::(a: A): ConsList[A] =
    Cons(a, this)

  /** reverse a [[ConsList]] */
  final def reverse: ConsList[A] = {
    var acc = empty[A]
    var l = this
    while(true){
      l match {
        case Cons(h, t) => acc = Cons(h , acc); l = t
        case CNil() => return acc
      }
    }
    acc
  }

  final def show(implicit A: Show[A]): String =
    this match {
      case CNil()     => "[]"
      case Cons(h, t) => "[" + t.foldLeft(A.show(h))(_ + "," + A.show(_)) + "]"
  }

  /** compute the size of a [[ConsList]] */
  final def size: Int = {
    var acc = 0
    var l = this
    while(true){
      l match {
        case Cons(_, t) =>
          acc = acc + 1
          l = t
        case CNil() => return acc
      }
    }
    acc
  }

  final def sort(implicit A: Order[A]): ConsList[A] =
    ConsList(toList.sorted(Order.ordering(A)): _*)


  /** get the tail if the [[ConsList]] is not empty */
  final def tailOption: Option[ConsList[A]] = this match {
    case CNil()     => Option.empty
    case Cons(_, t) => Some(t)
  }

  /** take the `n` first elements */
  final def take(n: Int): ConsList[A] = {
    var acc = empty[A]
    var l = this
    var m = n
    while(true){
      l match {
        case Cons(h, t) =>
          if(m > 0){ m = m - 1; l = t; acc = Cons(h, acc) }
          else return acc.reverse
        case CNil() => return acc.reverse
      }
    }
    acc
  }

  /** take elements as long as the predicate holds */
  final def takeWhile(p: A => Boolean): ConsList[A] = {
    var acc = empty[A]
    var l = this
    while(true){
      l match {
        case Cons(h, t) =>
          if(p(h)){ l = t; acc = Cons(h, acc) }
          else return acc.reverse
        case CNil() => return acc.reverse
      }
    }
    acc
  }

  /** transform a [[ConsList]] into a [[scala.List]] */
  final def toList: List[A] =
    foldLeft(ListBuffer.empty[A])(_ += _).toList

  final def traverse[G[_], B](f: A => G[B])(implicit G: Applicative[G]): G[ConsList[B]] =
    foldRight(G.pure(empty[B]))((a, acc) => G.map2(f(a), acc)(_ :: _))

  /** attempt to get head and tail of a [[ConsList]] */
  final def uncons: Option[(A, ConsList[A])] = this match {
    case CNil()     => Option.empty
    case Cons(h, t) => Some((h,t))
  }

  /** widen the type of a [[ConsList]] */
  final def widen[B >: A]: ConsList[B] =
    this.asInstanceOf[ConsList[B]]

  /** check if two [[ConsList]] are equal */
  final def ===(other: ConsList[A])(implicit A: Eq[A]): Boolean = {
    var as = this
    var bs = other
    while(true){
      (as, bs) match {
        case (CNil(), CNil()) => return true
        case (Cons(x, xs), Cons(y, ys)) =>
          if(A.eqv(x,y)){ as = xs; bs = ys }
          else return false
        case _ => return false
      }
    }
    true
  }

}

object ConsList extends ConsListInstances {
  final case class CNil[A]() extends ConsList[A]
  final case class Cons[A](head: A, tail: ConsList[A]) extends ConsList[A]

  private val nil: ConsList[Nothing] = CNil()

  /** create a [[ConsList]] with a single element */
  def singleton[A](a: A): ConsList[A] =
    Cons(a, empty)

  /** create an empty [[ConsList]] */
  def empty[A]: ConsList[A] =
    nil.asInstanceOf[ConsList[A]]

  /** create a [[ConsList]] from a varargs */
  def apply[A](as: A*): ConsList[A] =
    as.foldRight(empty[A])(Cons(_,_))

  def fill[A](n: Int)(a: A): ConsList[A] = {
    var acc = empty[A]
    var size = 0
    while(size < n){
      acc = a :: acc
      size = size + 1
    }
    acc
  }

}

sealed abstract class ConsListInstances {
  implicit def consListShow[A: Show]: Show[ConsList[A]] = Show.show(_.show)

  implicit def consListEq[A: Eq]: Eq[ConsList[A]] = new Eq[ConsList[A]]{
    override def eqv(x: ConsList[A], y: ConsList[A]): Boolean =
      x === y
  }

  implicit def conListMonoid[A]: Monoid[ConsList[A]] = new Monoid[ConsList[A]] {
    override def empty: ConsList[A] =
      ConsList.empty

    override def combine(x: ConsList[A], y: ConsList[A]): ConsList[A] =
      x concat y
  }

  implicit def consListTraverse: Traverse[ConsList] = new Traverse[ConsList] {
    override def traverse[G[_]: Applicative, A, B](fa: ConsList[A])(f: A => G[B]): G[ConsList[B]] =
      fa.traverse(f)

    override def foldLeft[A, B](fa: ConsList[A], b: B)(f: (B, A) => B): B =
      fa.foldLeft(b)(f)

    override def foldRight[A, B](fa: ConsList[A], b: B)(f: (A, B) => B): B =
      fa.foldRight(b)(f)

    override def map[A, B](fa: ConsList[A])(f: (A) => B): ConsList[B] =
      fa.map(f)

    override def partialFold[A, B](fa: ConsList[A])(f: A => Fold[B]): Fold[B] =
      fa.partialFold(f)
  }
}
