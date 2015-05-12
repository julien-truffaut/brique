package brique.tests

import brique.{ConsList, OrdSet}
import brique.OrdSet.internal.{Tip, Bin}
import scala.{Int, Long, None}

class OrdSetSpec extends BriqueSuite {

  test("apply toConList 1"){
    assert(OrdSet(1,2,3).toConsList === ConsList(1,2,3))
  }

  test("apply toConList 2"){
    assert(OrdSet(3,2,1).toConsList === ConsList(1,2,3))
  }

  test("equal"){
    check((as: ConsList[Int]) =>
      OrdSet.fromConsList(as) === OrdSet.fromConsList(as.reverse)
    )
  }

  test("toConList is sorted"){
    check((as: OrdSet[Int]) =>
      as.toConsList.isSorted
    )
  }

  test("contains successful"){
    check((a: Int, as: ConsList[Int]) =>
      OrdSet.fromConsList(as append a).contains(a)
    )
  }

  test("contains fail"){
    check((as: OrdSet[Int]) =>
      !as.map(_.toLong).contains(Long.MaxValue)
    )
  }

  test("insert"){
    check((a: Int, as: OrdSet[Int]) =>
      as.insert(a).contains(a)
    )
  }

  test("map"){
    check((as: ConsList[Int]) =>
      OrdSet.fromConsList(as).map(_ + 1) === OrdSet.fromConsList(as.map(_ + 1))
    )
  }

}
