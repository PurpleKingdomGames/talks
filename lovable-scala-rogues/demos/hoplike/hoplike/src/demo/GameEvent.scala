package demo

import indigo.*

enum GameEvent extends GlobalEvent:
  case NoOp
  case BumpWall
  case Exit
  case Move(pt: Point)
  case KillAt(pt: Point)
  case KillPlayer(enemyId: EnemyId, at: Point)
  case UpdateEnemies
  case StartPlayerTurn
  case LoadGame
  case ToggleLeapMode
  case Leap(from: Point, to: Point)
