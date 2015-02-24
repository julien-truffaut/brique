package brique.bench.input

import org.openjdk.jmh.annotations.{Scope, Setup, State}
import scala.{Int, Unit}

@State(Scope.Thread)
class IndexInput extends InputHelper {

  var index: Int = _

  @Setup
  def setup(): Unit =
    index = r.nextInt(1000)
}
