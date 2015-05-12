package brique.tests

import brique.ConsList
import brique.ConsList.{CNil, Cons}
import java.lang.String
import scala.{Boolean, Int, Option, None, Some}

class ConsListSpec extends BriqueSuite {

  test("apply") {
    assert(ConsList(1,2,3) === Cons(1, Cons(2, Cons(3, CNil()))))
  }


  test("toList") {
    check((as: ConsList[Int]) =>
      ConsList(as.toList: _*) === as
    )
  }

  test("foldLeft") {
    check((as: ConsList[Int]) =>
      as.foldLeft(ConsList.empty[Int])(_.prepend(_)).toList === as.toList.reverse
    )
  }

  test("map") {
    check((as: ConsList[Int]) =>
      as.map(_ + 1).toList === as.toList.map(_ + 1)
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
