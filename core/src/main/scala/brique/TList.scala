package brique


import scala.miniboxed

/**
 * shortest example to exhibit slowdown with miniboxing
 */
sealed abstract class TList[@miniboxed A] extends scala.Product with scala.Serializable {
  import brique.TList._

  final def reverse: TList[A] = {
    var acc: TList[A] = TNil[A]()
    var l = this
    while(true){
      l match {
        case c: TCons[A] => acc = TCons(c.head , acc); l = c.tail
        case _ => return acc
      }
    }
    acc
  }

}

object TList {
  final case class TNil[@miniboxed A]() extends TList[A]
  final case class TCons[@miniboxed A](head: A, tail: TList[A]) extends TList[A]
}
