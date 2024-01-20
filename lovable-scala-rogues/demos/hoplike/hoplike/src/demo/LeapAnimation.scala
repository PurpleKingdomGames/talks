package demo

import indigo.*
import indigo.syntax.*
import indigo.syntax.animations.*
import roguelikestarterkit.*
import roguelikestarterkit.syntax.*

object LeapAnimation:

  val moveGraphic: Graphic[TerminalMaterial] => SignalFunction[Point, Graphic[TerminalMaterial]] = g =>
    SignalFunction(pt => g.moveTo(pt))

  val moveAndScaleRainbowUp: (BlankEntity, Point) => SignalFunction[Double, BlankEntity] = { case (r, pt) =>
    SignalFunction(d =>
      r.transformTo(pt, Radians.zero, Vector2(1.0, -(d * 10)))
    )
  }

  val moveAndScaleRainbowDown: (BlankEntity, Point) => SignalFunction[Double, BlankEntity] = { case (r, pt) =>
    SignalFunction(d => r.transformTo(Point(pt.x, -5), Radians.zero, Vector2(1.0, d * (pt.y / ViewModel.squareSize.y))))
  }

  val vanishAndReappear: MoveAnim.Leaping => Timeline[Graphic[TerminalMaterial]] = leaping =>
    timeline(
      layer(
        show(Seconds(0.5))(_.moveTo(leaping.from * ViewModel.squareSize)),
        show(Seconds(1.4))(_ => Graphics.blankGraphic.moveTo(Point(-100))),
        show(Seconds(0.1))(_.moveTo(leaping.to * ViewModel.squareSize))
      )
    )

  val rainbowUp: MoveAnim.Leaping => Timeline[BlankEntity] = leaping =>
    timeline(
      layer(
        animate(0.5.second) { rainbow =>
          easeIn >>> lerp(0.0, 1.0) >>>
            moveAndScaleRainbowUp(
              rainbow,
              ((leaping.from + Point(0, 1)) * ViewModel.squareSize) - Point(0, 5)
            )
        }
      )
    )

  val rainbowDown: MoveAnim.Leaping => Timeline[BlankEntity] = leaping =>
    timeline(
      layer(
        startAfter(1.second),
        animate(1.second) { rainbow =>
          easeOut >>> lerp(0.0, 1.0) >>>
            moveAndScaleRainbowDown(
              rainbow,
              ((leaping.to + Point(0, 2)) * ViewModel.squareSize) - Point(0, 5)
            )
        }
      )
    )
