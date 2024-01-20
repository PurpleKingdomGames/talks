package demo

import indigo.*
import indigo.syntax.*
import scala.annotation.tailrec

object LevelGenerator:

  def nextLevel(dice: Dice, model: Model): Model =
    val holes = generateHoles(dice, model.floor)
    val enemies =
      generatateEnemies(dice, model.level, model.floor, holes.map(_.position), model.player.position, model.exit)

    model.copy(
      enemies = enemies,
      holes = holes,
      level = model.level + 1,
      exit = if (model.level + 1) % 2 == 0 then (model.room.size - 3).toPoint else Point(2),
      state = GameState.PlayersTurn,
      leapAvailable = true
    )

  def generateHoles(dice: Dice, floor: Rectangle): Batch[Hole] =
    val band =
      Rectangle(1, 3, floor.width, floor.height - 5)

    val initialHolePositions =
      (0 to 7).toBatch.map { _ =>
        Point(dice.rollFromZero(band.width), dice.rollFromZero(band.height)) + band.position
      }

    val holePositions =
      initialHolePositions.flatMap { pt =>
        if initialHolePositions.contains(pt + Point(0, -1)) || initialHolePositions.contains(pt + Point(0, 1)) then
          Batch(pt)
        else Batch(pt, pt + Point(0, 1))
      }

    holePositions.map { pt =>
      if holePositions.contains(pt + Point(0, -1)) && holePositions.contains(pt + Point(0, 1)) then
        Hole(pt, HoleStyle.Middle)
      else if holePositions.contains(pt + Point(0, -1)) then Hole(pt, HoleStyle.Bottom)
      else if holePositions.contains(pt + Point(0, 1)) then Hole(pt, HoleStyle.Top)
      else Hole(pt, HoleStyle.Middle)
    }

  def generatateEnemies(
      dice: Dice,
      level: Int,
      floor: Rectangle,
      holes: Batch[Point],
      player: Point,
      exit: Point
  ): Batch[Enemy] =
    @tailrec
    def rec(attempts: Int, acc: Batch[Point]): Batch[Enemy] =
      if attempts == 0 || acc.length == 3 + level then acc.map(pt => Enemy.spawn(dice, pt))
      else
        val pt = Point(dice.rollFromZero(floor.width), dice.rollFromZero(floor.height)) + floor.position

        if exit == pt || holes.contains(pt) || acc.contains(pt) || player.distanceTo(pt) < 3 then
          rec(attempts = attempts - 1, acc)
        else rec(attempts, pt :: acc)

    rec(32, Batch.empty)
