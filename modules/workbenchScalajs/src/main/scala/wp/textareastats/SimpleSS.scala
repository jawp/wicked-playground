package wp.textareastats

import scalatags.JsDom.all._
import scalatags.stylesheet.{Cls, StyleSheet}

object SimpleSS extends StyleSheet {
  val x: Cls = cls(
    backgroundColor := "red",
    height := 125
  )
  val y = cls.hover(
    opacity := 0.5
  )

  val z = cls(x.splice, y.splice)
}
