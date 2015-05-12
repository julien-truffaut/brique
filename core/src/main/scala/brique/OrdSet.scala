package brique

import java.lang.{Math, String}

import algebra.{Eq, Order}

import scala.{Boolean, Int, Option, None, Some, Nothing, Product, Serializable}
import scala.annotation.tailrec

/**
 * Set implemented as a balanced binary tree
 * Inspired by:
 *  - Purely Functional Data Structures by Chris Okasaki
 *  - haskell Data.Set
 *  - scalaz.ISet
 */
sealed abstract class OrdSet[A] extends Product with Serializable {
  import OrdSet._, OrdSet.internal._

  final def foldLeft[B](b: B)(f: (B, A) => B): B = this match {
    case Tip()           => b
    case Bin(_, a, l, r) => r.foldLeft(f(l.foldLeft(b)(f), a))(f)
  }

  final def foldRight[B](b: B)(f: (A, B) => B): B =
    foldLeft(b)((b, a) => f(a, b))


  /**
   * check if a set is empty
   * O(1)
   */
  final def isEmpty: Boolean = this match {
    case Tip()           => true
    case Bin(_, _, _, _) => false
  }


  /**
   * insert an element in a set
   * O(log n)
   */
  final def insert(a: A)(implicit A: Order[A]): OrdSet[A] = this match {
    case Tip()                => singleton(a)
    case f@Bin(s, elem, l, r) =>
      if(A.lt(a, elem))      balanceL(elem, l.insert(a), r)
      else if(A.lt(elem, a)) balanceR(elem, l          , r.insert(a))
      else this
  }

  final def map[B: Order](f: A => B): OrdSet[B] =
    foldLeft(empty[B])((acc, a) => acc.insert(f(a)))

  /**
   * check if an element is present in a set
   * O(log n)
   */
  final def contains(a: A)(implicit A: Order[A]): Boolean = this match {
    case Tip()              => false
    case Bin(_, elem, l, r) =>
      if(A.lt(a, elem))      l.contains(a)
      else if(A.lt(elem, a)) r.contains(a)
      else true
  }

  /**
   * number of elements in a set
   * O(1)
   */
  final def size: Int = this match {
    case Tip()           => 0
    case Bin(s, _, _, _) => s
  }

  final def toConsList: ConsList[A] =
    foldRight(ConsList.empty[A])(_ :: _).reverse

  override final def toString: String =
    _show(0, this)

  // TODO make it look nicer
  private final def _show(level: Int, set: OrdSet[A]): String =
    set match {
      case Tip()           => "()\n"
      case Bin(_, a, l, r) => ConsList.fill(level)("+").foldLeft("")(_ + _) + a.toString + "\n" + _show(level + 1, l) + _show(level + 1, r)
    }

  final def widen[B >: A]: OrdSet[B] =
    this.asInstanceOf[OrdSet[B]]

  final def ===(other: OrdSet[A])(implicit A: Eq[A]): Boolean =
    toConsList === other.toConsList

}

object OrdSet {
  import internal._

  def empty[A]: OrdSet[A] =
    _empty.asInstanceOf[OrdSet[A]]

  def singleton[A](a: A): OrdSet[A] =
    Bin(1, a, empty[A], empty[A])

  def apply[A: Order](as: A*): OrdSet[A] =
    as.foldLeft(empty[A])(_.insert(_))

  // TODO generalise to Foldable
  def fromConsList[A: Order](as: ConsList[A]): OrdSet[A] =
    as.foldLeft(empty[A])(_.insert(_))

  def complete[A: Order](a: A, i: Int): OrdSet[A] = {
    var set = empty[A]
    var d = 0
    while(d < i){
      set = Bin(set.size * 2 + 1, a, set, set)
      d = d + 1
    }
    set
  }

  def balanceL[A](a: A, l: OrdSet[A], r: OrdSet[A]): OrdSet[A] = r match {
    case Tip() =>
      l match {
        case Tip() =>
          singleton(a)
        case Bin(_, _, Tip(), Tip()) =>
          Bin(2, a, l, empty)
        case Bin(_, la, Tip(), Bin(_, lra, _, _)) =>
          Bin(3, lra, singleton(la), singleton(a))
        case Bin(_, la, ll@Bin(_, _, _, _), Tip()) =>
          Bin(3, la, ll, singleton(a))
        case Bin(ls, la, ll@Bin(lls, _, _, _), lr@Bin(lrs, lra, lrl, lrr)) =>
          if (lrs < RATIO * lls) Bin(1 + ls, la, ll, Bin(1 +lrs, a, lr, empty))
          else Bin(1 + ls, lra, Bin(lls + 1 + lrl.size, la, ll, lrl), Bin(1 + lrl.size, a, lrr, empty))
      }
    case Bin(rs, _, _, _) =>
      l match {
        case Tip() =>
          Bin(1 + rs, a, Tip(), r)
        case Bin(ls, la, ll, lr) =>
          if (l.size > DELTA * r.size) {
            (ll, lr) match {
              case (Bin(lls, _, _, _), Bin(lrs, lra, lrl, lrr)) =>
                if (lrs < RATIO * lls) Bin(1 + ls + rs, la, ll, Bin(1 + rs + lrs, a, lr, r))
                else Bin(1 + ls + rs, lra, Bin(1 + lls + lrl.size, la, ll, lrl), Bin(1 + lls + lrl.size, a, lrr, r))
              case _ => scala.sys.error("Failure in OrdSet.balanceL")
            }
          } else Bin(1 + ls + rs, a, l, r)
      }
  }

  def balanceR[A](a: A, l: OrdSet[A], r: OrdSet[A]): OrdSet[A] =
    l match {
      case Tip() =>
        r match {
          case Tip() =>
            singleton(a)
          case Bin(_, _, Tip(), Tip()) =>
            Bin(2, a, empty, r)
          case Bin(_, ra, Tip(), rr@Bin(_, _, _, _)) =>
            Bin(3, ra, singleton(a), rr)
          case Bin(_, ra, Bin(_, rla, _, _), Tip()) =>
            Bin(3, rla, singleton(a), singleton(ra))
          case Bin(rs, ra, rl@Bin(rls, rla, rll, rlr), rr@Bin(rrs, _, _, _)) =>
            if (rls < RATIO * rrs) Bin(1 + rs, ra, Bin(1 + rls, a, empty, rl), rr)
            else Bin(1 + rs, rla, Bin(1 + rll.size, a, empty, rll), Bin(1 + rrs + rlr.size, ra, rlr, rr))
        }
      case Bin(ls, _, _, _) =>
        r match {
          case Tip() =>
            Bin(ls + 1, a, l, empty)
          case Bin(rs, ra, rl, rr) =>
            if (r.size > DELTA * l.size) {
              (rl, rr) match {
                case (Bin(rls, rla, rll, rlr), Bin(rrs, _, _, _)) =>
                  if (rls < RATIO * rrs) Bin(1 + ls + rs, ra, Bin(1 + ls + rls, a, l, rl), rr)
                  else Bin(1 + ls + rs, rla, Bin(1 + ls + rll.size, a, l, rll), Bin(1 + rrs + rlr.size, ra, rlr, rr))
                case _ => scala.sys.error("Failure in OrdSet.balanceR")
              }
            } else Bin(1 + ls + rs, a, l, r)
        }
    }


  object internal {
    final case class Tip[A]() extends OrdSet[A]
    final case class Bin[A](_size: Int, elem: A, left: OrdSet[A], right: OrdSet[A]) extends OrdSet[A]

    val _empty: OrdSet[Nothing] = Tip()
    val RATIO: Int = 2
    val DELTA: Int = 3
  }
}

