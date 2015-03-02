package brique.internal

import scala.{Boolean, Long}

object BitsUtil {

  def zero(key: Long, mask: Long): Boolean =
    (key & mask) == 0

  def noMatch(key: Long, prefix: Long, m: Long): Boolean =
    mask(key, m) != prefix

  def doMatch(key: Long, prefix: Long, m: Long): Boolean =
    mask(key, m) == prefix

  def branchMask(p1: Long, p2: Long): Long =
    java.lang.Long.highestOneBit(p1 ^ p2)

  def mask(key: Long, m: Long): Long =
    key & (~(m - 1) ^ m)

}
