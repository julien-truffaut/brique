package brique.tests

import brique.{ConsList, OrdSet}

import scala.App

object Test extends App with TestInstances {
  scala.Predef.println(OrdSet(1, 0).toString)
  scala.Predef.println(OrdSet(1, 0).contains(0))
}
