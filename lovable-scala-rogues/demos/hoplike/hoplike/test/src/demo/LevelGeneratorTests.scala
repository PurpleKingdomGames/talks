package demo

import indigo.*

class LevelGeneratorTests extends munit.FunSuite {

  // This would be better as a property based test!
  test("placing enemies") {

    val floor  = Rectangle(1, 1, 5, 5)
    val level  = 0
    val holes  = Batch(Point(1, 2), Point(2, 2), Point(3, 2))
    val player = Point(1)
    val exit   = Point.zero

    val actual =
      LevelGenerator.generatateEnemies(Dice.fromSeed(128), level, floor, holes, player, exit)

    assert(actual.length == 3)
    assert(actual.forall(_.position.distanceTo(player) >= 3))
    assert(actual.forall(e => !holes.contains(e.position)))

  }

}
