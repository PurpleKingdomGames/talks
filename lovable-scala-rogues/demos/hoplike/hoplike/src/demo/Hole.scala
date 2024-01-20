package demo

import indigo.*

final case class Hole(position: Point, style: HoleStyle)

enum HoleStyle:
  case Top, Middle, Bottom
