package demo

import indigo.*
import indigo.syntax.*
import roguelikestarterkit.*
import roguelikestarterkit.syntax.*
import scala.annotation.tailrec
import demo.generated.Assets
import indigoextras.pathfinding.PathFinder
import indigoextras.pathfinding.PathBuilder

final case class Model(
    player: Player,
    room: Rectangle,
    exit: Point,
    enemies: Batch[Enemy],
    holes: Batch[Hole],
    state: GameState,
    level: Int,
    leapAvailable: Boolean
):
  val floor: Rectangle = room.contract(1)

  def movePlayer: Model =
    this.copy(state = GameState.PlayerMoving)

  def endPlayerTurn: Model =
    movePlayer

  def moveEnemies: Model =
    this.copy(state = GameState.EnemyMoving)

  def playersTurn: Model =
    this.copy(state = GameState.PlayersTurn)

  def gameOver(timeOfDeath: Seconds): Model =
    this.copy(
      player = player.kill(timeOfDeath),
      state = GameState.GameOver
    )

  def leapMode: Model =
    this.copy(state = GameState.Leap)

  def toggleLeapMode: Model =
    this.copy(
      state = state match
        case GameState.Leap        => GameState.PlayersTurn
        case GameState.PlayersTurn => GameState.Leap
        case s                     => s
    )

  def bump(pt: Point): GameEvent =
    pt match
      case p if !floor.contains(p)                                => GameEvent.BumpWall
      case p if enemies.exists(e => e.position == p && !e.isDead) => GameEvent.KillAt(p)
      case p if holes.exists(_.position == p)                     => GameEvent.NoOp
      case p if state.inLeapMode                                  => GameEvent.Leap(player.position, p)
      case p                                                      => GameEvent.Move(p)

object Model:
  def initial(size: Size): Model =
    Model(
      Player(Point(2), PlayerState.Alive),
      Rectangle(size),
      (size - 3).toPoint,
      Batch.empty,
      Batch.empty,
      GameState.Loading,
      -1,
      true
    )

  val leapOffsets: Batch[Point] =
    Batch(
      Point(-2, -2),
      Point(2, -2),
      Point(-1, -1),
      Point(1, -1),
      Point(-1, 1),
      Point(1, 1),
      Point(-2, 2),
      Point(2, 2)
    )

  val moveOffsets: Batch[Point] =
    Batch(
      Point(-1, 0),
      Point(1, 0),
      Point(0, -1),
      Point(0, 1)
    )

  def updateModel(context: FrameContext[Size], model: Model): GlobalEvent => Outcome[Model] =
    case e: GameEvent =>
      Model.handleGameEvent(context, model)(e)

    case KeyboardEvent.KeyDown(Key.LEFT_ARROW) if model.state.isPlayersTurn =>
      Outcome(model).addGlobalEvents(model.bump(model.player.position.moveBy(-1, 0)))

    case KeyboardEvent.KeyDown(Key.RIGHT_ARROW) if model.state.isPlayersTurn =>
      Outcome(model).addGlobalEvents(model.bump(model.player.position.moveBy(1, 0)))

    case KeyboardEvent.KeyDown(Key.UP_ARROW) if model.state.isPlayersTurn =>
      Outcome(model).addGlobalEvents(model.bump(model.player.position.moveBy(0, -1)))

    case KeyboardEvent.KeyDown(Key.DOWN_ARROW) if model.state.isPlayersTurn =>
      Outcome(model).addGlobalEvents(model.bump(model.player.position.moveBy(0, 1)))

    case KeyboardEvent.KeyUp(Key.SPACE) if model.player.state.isDead =>
      restart(model, context.running)

    case KeyboardEvent.KeyUp(Key.SPACE) if model.state.isPlayersTurn && model.leapAvailable =>
      Outcome(model.leapMode)

    case KeyboardEvent.KeyUp(Key.SPACE) if model.state.inLeapMode =>
      Outcome(model.playersTurn)

    case MouseEvent.Click(pt) if model.player.state.isDead =>
      restart(model, context.running)

    case MouseEvent.Click(pt) if model.state.inLeapMode =>
      leapOffsets
        .find { pt =>
          model.player.position + pt == context.mouse.position / 16 / 3
        } match
        case None =>
          Outcome(model)

        case Some(move) =>
          Outcome(model.copy(leapAvailable = false))
            .addGlobalEvents(model.bump(model.player.position.moveBy(move)))

    case MouseEvent.Click(pt) if model.state.isPlayersTurn =>
      // If all the enemies are dead, you can just keep clicking on the
      // exit / wherever and the pathfinder will get you there (probably)
      if model.enemies.forall(_.isDead) then
        val impassable = model.holes.map(_.position - model.floor.position)

        val pf =
          PathFinder.findPath(
            start = model.player.position - model.floor.position,
            end = context.mouse.position / 16 / 3 - model.floor.position,
            PathBuilder.fromAllowedPoints(
              allowedPoints = model.floor.moveTo(Point.zero).toPoints.filterNot(impassable.contains).toSet,
              allowedMovements = PathBuilder.Movements.Side,
              directSideCost = 1,
              diagonalCost = 1,
              maxHeuristicFactor = 1
            )
          )

        val proposed =
          pf match
            case None =>
              model.player.position - model.floor.position

            case Some(moves) if moves.isEmpty =>
              model.player.position - model.floor.position

            case Some(Batch(move)) =>
              move

            case Some(moves) =>
              moves.drop(1).head

        Outcome(model)
          .addGlobalEvents(model.bump(model.player.position.moveTo(proposed + model.floor.position)))
      else
        // Otherwise you have to click on the valid squares
        moveOffsets
          .find { pt =>
            model.player.position + pt == context.mouse.position / 16 / 3
          } match
          case None =>
            Outcome(model)

          case Some(move) =>
            Outcome(model).addGlobalEvents(model.bump(model.player.position.moveBy(move)))

    case _ =>
      Outcome(model)

  def restart(model: Model, running: Seconds): Outcome[Model] =
    val waitedRespectfulPeriod =
      model.player.state.proclaimTimeOfDeath.map(t => running - t > Seconds(1)).getOrElse(true)

    if waitedRespectfulPeriod then
      Outcome(
        Model.initial(model.room.size)
      ).addGlobalEvents(GameEvent.LoadGame)
    else Outcome(model)

  def handleGameEvent(context: FrameContext[Size], model: Model): GameEvent => Outcome[Model] =
    case GameEvent.Leap(_, to) =>
      Outcome(model.copy(player = model.player.moveTo(to)).endPlayerTurn)
        .addGlobalEvents(Assets.assets.rainbowPlay)

    case GameEvent.ToggleLeapMode =>
      Outcome(model.toggleLeapMode)

    case GameEvent.LoadGame =>
      Outcome(LevelGenerator.nextLevel(context.dice, model))

    case GameEvent.NoOp =>
      Outcome(model)

    case GameEvent.BumpWall =>
      Outcome(model)

    case GameEvent.Exit =>
      Outcome(LevelGenerator.nextLevel(context.dice, model))
        .addGlobalEvents(Assets.assets.descendPlay)

    case GameEvent.Move(pt) =>
      Outcome(model.copy(player = model.player.moveTo(pt)).endPlayerTurn)
        .addGlobalEvents(PlaySound(Assets.assets.move1, Volume.Max))

    case GameEvent.KillAt(pt) =>
      Outcome(
        model
          .copy(enemies = model.enemies.map(e => if e.position == pt then e.kill else e))
          .endPlayerTurn
      )
        .addGlobalEvents(PlaySound(Assets.assets.attack, Volume.Max))

    case GameEvent.KillPlayer(_, _) =>
      Outcome(model.gameOver(context.running))
        .addGlobalEvents(PlaySound(Assets.assets.attack, Volume.Max))

    case GameEvent.UpdateEnemies =>
      @tailrec
      def rec(
          remaining: Batch[Enemy],
          livingObstacles: Batch[Point],
          alive: Batch[Outcome[Enemy]],
          dead: Batch[Outcome[Enemy]]
      ): Batch[Outcome[Enemy]] =
        if remaining.isEmpty then dead ++ alive
        else
          remaining match
            case e ==: es if e.isDead =>
              rec(es, livingObstacles, alive, dead :+ Outcome(e))

            case e ==: es =>
              val living     = livingObstacles ++ es.map(_.position)
              val impassable = model.exit :: living ++ model.holes.map(_.position).map(_ - model.floor.position)

              val pf =
                PathFinder.findPath(
                  start = e.position - model.floor.position,
                  end = model.player.position - model.floor.position,
                  PathBuilder.fromAllowedPoints(
                    allowedPoints = model.floor.moveTo(Point.zero).toPoints.filterNot(impassable.contains).toSet,
                    allowedMovements = PathBuilder.Movements.Side,
                    directSideCost = 1,
                    diagonalCost = 1,
                    maxHeuristicFactor = 1
                  )
                )

              val proposed =
                pf match
                  case None =>
                    e.position - model.floor.position

                  case Some(moves) if moves.isEmpty =>
                    e.position - model.floor.position

                  case Some(Batch(move)) =>
                    move

                  case Some(moves) =>
                    moves.drop(1).head

              val next = e.bump(proposed + model.floor.position, model.player.position)

              rec(es, e.position :: livingObstacles, alive :+ next, dead)

            case _ =>
              // Won't get here, Batch syntax limitation.
              rec(Batch.empty, livingObstacles, alive, dead)

      val updatedEnemies =
        rec(model.enemies, Batch.empty, Batch.empty, Batch.empty)

      updatedEnemies.sequence
        .map { next =>
          model.copy(enemies = next).moveEnemies
        }
        .addGlobalEvents(PlaySound(Assets.assets.move2, Volume.Max))

    case GameEvent.StartPlayerTurn =>
      if model.player.state.isDead || model.state.isGameOver then Outcome(model)
      else Outcome(model.playersTurn)

enum GameState:
  case Loading, PlayersTurn, PlayerMoving, EnemyMoving, GameOver, Leap

  def isLoading: Boolean =
    this match
      case Loading => true
      case _       => false

  def isPlayersTurn: Boolean =
    this match
      case PlayersTurn => true
      case _           => false

  def movingPlayer: Boolean =
    this match
      case PlayerMoving => true
      case _            => false

  def movingEnemies: Boolean =
    this match
      case EnemyMoving => true
      case _           => false

  def isGameOver: Boolean =
    this match
      case GameOver => true
      case _        => false

  def inLeapMode: Boolean =
    this match
      case Leap => true
      case _    => false
