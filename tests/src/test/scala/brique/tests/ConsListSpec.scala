package brique.tests

import algebra.{Monoid, Eq}
import brique.ConsList
import brique.ConsList.{CNil, Cons}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import java.lang.String
import scala.{Boolean, Int, Option, None, Some}
import scala.miniboxed

class ConsListSpec extends FunSuite with Checkers {

  implicit val intEq = new Eq[Int] {
    def eqv(x: Int, y: Int): Boolean = x == y
  }

  implicit def optionEq[@miniboxed A](implicit A: Eq[A]): Eq[Option[A]] = new Eq[Option[A]] {
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

  implicit def consListArb[@miniboxed A](implicit A: Arbitrary[A]): Arbitrary[ConsList[A]] = Arbitrary(
    Gen.listOf(A.arbitrary).map(l => ConsList(l: _*))
  )

  test("apply") {
    assert(ConsList(1,2,3) === Cons(1, Cons(2, Cons(3, CNil()))))
  }


  test("toList") {
    check((as: ConsList[Int]) =>
      ConsList(as.toList: _*) === as
    )
  }

  test("foldLeft with cons reverse the list") {
    check((as: ConsList[Int]) =>
      as.foldLeft(ConsList.empty[Int])(_.prepend(_)).toList === as.toList.reverse
    )
  }

  test("foldMap") {
    check((as: ConsList[Int]) =>
      as.foldMap(ConsList.empty[Int])(ConsList.singleton) === as
    )
  }

  test("foldMap order") {
    assert(ConsList(1,2,3).foldMap("")(_.toString) == "123")
  }

  test("flatMap") {
    check((as: ConsList[Int]) =>
      as.flatMap(ConsList.singleton) === as
    )
  }

  test("cons - uncons") {
    check((h: Int, t: ConsList[Int]) =>
      (h :: t).uncons === Some((h, t))
    )
  }

  test("++") {
    check((xs: ConsList[Int], ys: ConsList[Int]) =>
      xs ++ ys === ConsList(xs.toList ++ ys.toList: _*)
    )
  }

  test("append") {
    assert(ConsList(1,2,3).append(4) === ConsList(1,2,3,4))
  }

  test("filter"){
    assert(ConsList(1,2,3,4,5).filter(_ <= 2) === ConsList(1,2))
  }

  test("lookup"){
    assert(ConsList(-1,0,1,2,3).map(i => ConsList(0,1,2).lookup(i)) === ConsList(None, Some(0), Some(1), Some(2), None))
  }

  test("take"){
    assert(ConsList(1,2,3,4,5).take(2) === ConsList(1,2))
  }

  test("takeWhile"){
    assert(ConsList(1,2,3,4,5).takeWhile(_ <= 3) === ConsList(1,2,3))
  }

  test("drop"){
    assert(ConsList(1,2,3,4,5).drop(2) === ConsList(3,4,5))
  }

  test("dropWhile"){
    assert(ConsList(1,2,3,4,5).dropWhile(_ <= 3) === ConsList(4,5))
  }

  test("widen"){
    sealed trait Fruit
    case object Apple  extends Fruit
    case object Orange extends Fruit

    val apples: ConsList[Apple.type] = ConsList(Apple, Apple)

    // Orange :: apples doesn't compile
    assert((Orange :: apples.widen[Fruit]) == ConsList[Fruit](Orange, Apple, Apple))
  }

}
