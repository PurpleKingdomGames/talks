package demo

import indigo.*
import indigo.syntax.*
import roguelikestarterkit.*
import roguelikestarterkit.syntax.*
import demo.generated.*

final case class ViewModel(bg: TerminalClones, player: MoveAnim, enemies: Map[EnemyId, MoveAnim])

object ViewModel:

  import indigo.syntax.animations.*

  val empty: ViewModel =
    ViewModel(TerminalClones.empty, MoveAnim.initial(Point.zero), Map())

  def initial(dice: Dice, model: Model): Outcome[ViewModel] =
    val floorTiles = model.floor.toPoints.map { pt =>
      pt -> {
        if dice.roll(5) != 5 then MapTile(Tile.LIGHT_SHADE, RGBA.White, RGBA.Black)
        else MapTile(Tile.MEDIUM_SHADE, RGBA.White, RGBA.Black)
      }
    }

    val terminal =
      RogueTerminalEmulator(model.room.size)
        .fill(MapTile(Tile.DARK_SHADE, RGBA.Yellow, RGBA.Orange))
        .put(floorTiles)

    val terminalClones =
      terminal
        .toCloneTiles(
          CloneId("tile"),
          Point.zero,
          RoguelikeTiles.Size16x16.charCrops
        ) { (fg, bg) =>
          Graphic(
            16,
            16,
            TerminalMaterial(Assets.assets.tilesheet, fg, bg)
              .withLighting(
                LightingModel.Lit.flat
                  .withEmissive(Assets.assets.tilesheetEmissive, 1.0)
                  .withNormal(Assets.assets.tilesheetNormal, 1.0)
              )
          )
        }

    val enemies =
      model.enemies
        .map(e => e.id -> MoveAnim.Moving(e.position, e.position, Seconds.zero))
        .toMap

    Outcome(
      ViewModel(
        terminalClones,
        MoveAnim.Moving(model.player.position, model.player.position, Seconds.zero),
        enemies
      )
    )

  val squareSize: Point = Point(16)

  val move: Graphic[TerminalMaterial] => SignalFunction[Point, Graphic[TerminalMaterial]] = g =>
    SignalFunction(pt => g.moveTo(pt))

  val slideAnimation: MoveAnim.Moving => Timeline[Graphic[TerminalMaterial]] = moving =>
    timeline(
      layer(
        animate(moving.duration) { graphic =>
          easeInOut >>> lerp(moving.from * squareSize, moving.to * squareSize) >>> move(graphic)
        }
      )
    )

  val attackAnimation: MoveAnim.Attacking => Timeline[Graphic[TerminalMaterial]] = attacking =>
    val target = attacking.to * ViewModel.squareSize
    val origin = attacking.from * ViewModel.squareSize

    timeline(
      layer(
        animate(attacking.duration) { graphic =>
          SignalFunction[Seconds, Graphic[TerminalMaterial]] { t =>
            val curve  = Bezier(origin.toVertex, target.toVertex, origin.toVertex)
            val signal = curve.toSignal(attacking.duration)

            // Vertex has a .toPoint method, but this removes wobble caused by rounding.
            val toPoint = SignalFunction { (v: Vertex) =>
              Point(
                x = if Math.abs(v.x.toDouble - origin.x.toDouble) <= 1 then origin.x else v.x.toInt,
                y = if Math.abs(v.y.toDouble - origin.y.toDouble) <= 1 then origin.y else v.y.toInt
              )
            }

            (signal |> toPoint >>> move(graphic)).at(t)
          }
        }
      )
    )

  def updateViewModel(
      context: FrameContext[Size],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    case GameEvent.StartPlayerTurn =>
      val nextPlayer =
        MoveAnim.Moving(model.player.position, model.player.position, context.running)

      Outcome(
        viewModel.copy(
          player = nextPlayer
        )
      )

    case GameEvent.LoadGame =>
      ViewModel.initial(context.dice, model)

    case GameEvent.Exit =>
      ViewModel.initial(context.dice, model)

    case GameEvent.Leap(from, to) =>
      val nextPlayer =
        MoveAnim.Leaping(from, to, context.running)

      Outcome(
        viewModel.copy(
          player = nextPlayer
        )
      )

    case GameEvent.KillAt(pt) =>
      val nextPlayer =
        MoveAnim.Attacking(model.player.position, pt, context.running)

      Outcome(
        viewModel.copy(
          player = nextPlayer
        )
      )

    case GameEvent.KillPlayer(id, at) =>
      Outcome(
        viewModel.copy(
          enemies = viewModel.enemies.map { case (eId, e) =>
            if eId == id then
              model.enemies.find(_.id == id) match
                case None =>
                  eId -> e

                case Some(en) =>
                  eId -> MoveAnim.Attacking(en.position, at, context.running)
            else eId -> e
          }
        )
      )

    case FrameTick if model.state.movingPlayer =>
      viewModel.player match
        case a @ MoveAnim.Attacking(_, _, lastUpdate) =>
          val attackFinished = context.running > viewModel.player.duration + lastUpdate

          val nextPlayer =
            if attackFinished then MoveAnim.Moving(model.player.position, model.player.position, context.running)
            else a

          Outcome(
            viewModel.copy(
              player = nextPlayer
            )
          ).addGlobalEvents(
            if attackFinished then Batch(GameEvent.UpdateEnemies)
            else Batch.empty
          )

        case MoveAnim.Leaping(_, to, lastUpdate) =>
          val nextPlayer =
            if model.player.position != to then MoveAnim.Leaping(to, model.player.position, context.running)
            else viewModel.player

          val nextLastUpdate =
            if model.player.position != to then context.running
            else lastUpdate

          Outcome(
            viewModel.copy(
              player = nextPlayer
            )
          ).addGlobalEvents(
            if context.running > viewModel.player.duration + nextLastUpdate then
              if model.player.position == model.exit then Batch(GameEvent.Exit)
              else Batch(GameEvent.UpdateEnemies)
            else Batch.empty
          )

        case MoveAnim.Moving(_, to, lastUpdate) =>
          val nextPlayer =
            if model.player.position != to then MoveAnim.Moving(to, model.player.position, context.running)
            else viewModel.player

          val nextLastUpdate =
            if model.player.position != to then context.running
            else lastUpdate

          Outcome(
            viewModel.copy(
              player = nextPlayer
            )
          ).addGlobalEvents(
            if context.running > viewModel.player.duration + nextLastUpdate then
              if model.player.position == model.exit then Batch(GameEvent.Exit)
              else Batch(GameEvent.UpdateEnemies)
            else Batch.empty
          )

    case FrameTick if model.state.movingEnemies =>
      val missing =
        model.enemies
          .filterNot(e => viewModel.enemies.exists { case (id, _) => id == e.id })
          .map(e => e.id -> MoveAnim.Moving(e.position, e.position, context.running))

      val nextEnemies: Map[EnemyId, MoveAnim] =
        (viewModel.enemies ++ missing.toMap)
          .filter { case (id, _) =>
            model.enemies.exists(_.id == id)
          }
          .map { case (id, anim) =>
            id -> {
              anim match
                case MoveAnim.Attacking(from, to, lastUpdate) =>
                  model.enemies
                    .find(_.id == id)
                    .map { e =>
                      val attackFinished = context.running > anim.duration + lastUpdate

                      if attackFinished then MoveAnim.Moving(e.position, e.position, context.running)
                      else anim
                    }
                    .getOrElse(anim)

                case MoveAnim.Leaping(_, _, _) =>
                  anim

                case MoveAnim.Moving(from, to, lastUpdate) =>
                  model.enemies
                    .find(_.id == id)
                    .map { e =>
                      if e.position != anim.giveTo then MoveAnim.Moving(anim.giveTo, e.position, context.running)
                      else anim
                    }
                    .getOrElse(anim)

            }
          }

      val timeElapsed =
        MoveAnim.MovingDuration + viewModel.enemies.headOption.map(_._2.giveLastUpdate).getOrElse(Seconds.zero)

      Outcome(
        viewModel.copy(
          enemies = nextEnemies
        )
      ).addGlobalEvents(
        if context.running > timeElapsed then Batch(GameEvent.StartPlayerTurn)
        else Batch.empty
      )

    case _ =>
      Outcome(viewModel)

enum MoveAnim(val duration: Seconds):
  case Attacking(from: Point, to: Point, lastUpdate: Seconds) extends MoveAnim(MoveAnim.MovingDuration)
  case Leaping(from: Point, to: Point, lastUpdate: Seconds)   extends MoveAnim(MoveAnim.LeapingDuration)
  case Moving(from: Point, to: Point, lastUpdate: Seconds)    extends MoveAnim(MoveAnim.MovingDuration)

  def giveLastUpdate: Seconds =
    this match
      case Attacking(_, _, lastUpdate) => lastUpdate
      case Leaping(_, _, lastUpdate)   => lastUpdate
      case Moving(_, _, lastUpdate)    => lastUpdate

  def giveFrom: Point =
    this match
      case Attacking(from, _, _) => from
      case Leaping(from, _, _)   => from
      case Moving(from, _, _)    => from

  def giveTo: Point =
    this match
      case Attacking(_, to, _) => to
      case Leaping(_, to, _)   => to
      case Moving(_, to, _)    => to

object MoveAnim:
  val MovingDuration: Seconds  = Seconds(0.25)
  val LeapingDuration: Seconds = Seconds(2)

  def initial(pt: Point): Moving =
    Moving(pt, pt, Seconds.zero)
