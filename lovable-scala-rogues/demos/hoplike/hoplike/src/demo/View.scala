package demo

import indigo.*
import indigo.syntax.*
import roguelikestarterkit.*
import generated.*
import indigo.shared.scenegraph.AmbientLight

object View:

  def present(
      context: FrameContext[Size],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    if model.state.isLoading then Outcome(SceneUpdateFragment.empty)
    else
      val moveHighlights =
        if model.state.isGameOver then Batch.empty
        else
          val offsets =
            if model.state.inLeapMode then Model.leapOffsets
            else Model.moveOffsets

          offsets
            .map(pt => model.player.position + pt)
            .flatMap { pt =>
              if model.floor.contains(pt) && !model.holes.exists(_.position == pt) then
                Batch(Graphics.yellowHighlight.moveTo(pt * 16))
              else Batch.empty
            }

      val hover =
        if model.state.isGameOver then Batch.empty
        else Batch(Graphics.cyanHighlight.moveTo((context.mouse.position / 16 / 3) * 16))

      val holes =
        model.holes.map { hole =>
          hole.style match
            case HoleStyle.Top =>
              Graphics.holeTopGraphic.moveTo(hole.position * ViewModel.squareSize)

            case HoleStyle.Middle =>
              Graphics.holeMiddleGraphic.moveTo(hole.position * ViewModel.squareSize)

            case HoleStyle.Bottom =>
              Graphics.holeBottomGraphic.moveTo(hole.position * ViewModel.squareSize)

        }

      val player =
        viewModel.player match
          case attacking: MoveAnim.Attacking =>
            val p =
              ViewModel
                .attackAnimation(attacking)
                .atOrLast(context.running - attacking.lastUpdate)(Graphics.playerGraphic)
                .getOrElse(Graphics.playerGraphic.moveTo(model.player.position * ViewModel.squareSize))

            Batch(
              p,
              Graphics.shadow.moveTo(p.position + Point(8, 14))
            )

          case leaping: MoveAnim.Leaping =>
            val p =
              LeapAnimation
                .vanishAndReappear(leaping)
                .atOrLast(context.running - leaping.lastUpdate)(Graphics.playerGraphic)
                .getOrElse(Graphics.playerGraphic.moveTo(model.player.position * ViewModel.squareSize))

            val rainbowUp =
              LeapAnimation
                .rainbowUp(leaping)
                .atOrLast(context.running - leaping.lastUpdate)(Graphics.rainbowBox)
                .toBatch

            val rainbowDown =
              LeapAnimation
                .rainbowDown(leaping)
                .at(context.running - leaping.lastUpdate)(Graphics.rainbowBox)
                .toBatch

            rainbowUp ++ rainbowDown ++ Batch(
              p,
              Graphics.shadow.moveTo(p.position + Point(8, 14))
            )

          case moving: MoveAnim.Moving =>
            val p =
              ViewModel
                .slideAnimation(moving)
                .atOrLast(context.running - moving.lastUpdate)(Graphics.playerGraphic)
                .getOrElse(Graphics.playerGraphic.moveTo(model.player.position * ViewModel.squareSize))

            Batch(
              p,
              Graphics.shadow.moveTo(p.position + Point(8, 14))
            )

      val exit =
        Graphics.exitGraphic
          .moveTo(model.exit * ViewModel.squareSize)

      val npcs =
        val shadowPos = Point(8, 15)

        viewModel.enemies.toBatch
          .flatMap { case (id, anim) =>
            model.enemies
              .find(_.id == id)
              .map { e =>
                val npcGraphic =
                  if e.isDead then Graphics.enemyDeadGraphic else Graphics.enemyGraphic

                val graphic =
                  anim match
                    case attacking: MoveAnim.Attacking =>
                      ViewModel
                        .attackAnimation(attacking)
                        .atOrLast(context.running - attacking.lastUpdate)(npcGraphic)
                        .getOrElse(Graphics.playerGraphic.moveTo(model.player.position * ViewModel.squareSize))

                    case leaping: MoveAnim.Leaping =>
                      // Enemies do not leap
                      npcGraphic.moveTo(e.position * ViewModel.squareSize)

                    case moving: MoveAnim.Moving =>
                      ViewModel
                        .slideAnimation(moving)
                        .atOrLast(context.running - moving.lastUpdate)(npcGraphic)
                        .getOrElse(npcGraphic.moveTo(e.position * ViewModel.squareSize))

                e.isDead -> Batch(Graphics.shadow.moveTo(graphic.position + shadowPos), graphic)
              }
              .toBatch
          }
          .sortBy(!_._1)
          .flatMap(_._2)

      val statusText =
        val text =
          model.player.state match
            case PlayerState.Dead(timeOfDeath) if context.running - timeOfDeath > Seconds(1) =>
              Signal.Pulse(Seconds(0.5)).map(p => if p then "Retry?" else "").at(context.running)

            case _ =>
              "lvl " + model.level

        Text(
          text,
          RoguelikeTiles.Size16x16.Fonts.fontKey,
          TerminalMaterial(Assets.assets.tilesheet, RGBA.fromHexString("9f7242"), RGBA.Zero)
        ).moveTo(Point(0, 10 * 16 * 3) + Point(12, 14))

      val blendMaterial =
        model.player.state match
          case PlayerState.Alive =>
            BlendMaterial.Normal

          case PlayerState.Dead(timeOfDeath) =>
            BlendMaterial.BlendEffects.None.withSaturation(
              Signal.Linear(Seconds(1)).map(d => 1.0 - d).at(context.running - timeOfDeath)
            )

      val lights =
        Batch(
          AmbientLight(RGBA.Blue.mix(RGBA.White).withAlpha(0.5)),
          PointLight(Point(5) * ViewModel.squareSize, RGBA.White, RGBA.White, 0.6, Falloff.SmoothQuadratic(0, 5 * 16)),
          PointLight(
            model.exit * ViewModel.squareSize,
            RGBA.White,
            RGBA.White,
            0.6,
            Falloff.SmoothQuadratic(0, 3 * 16)
          ),
          DirectionLight(Radians.fromDegrees(0), RGBA(0.75, 1.0, 1.0, 0.25))
        )

      val mouseGridSquare     = context.mouse.position / 16 / 3
      val leapButtonSquare    = Point(8, 10)
      val mouseOverLeapButton = mouseGridSquare == leapButtonSquare

      val leapIcon =
        if model.state.inLeapMode then Graphics.leapActive
        else if !model.leapAvailable then Graphics.leapUnavailable
        else if mouseOverLeapButton then Graphics.leapActive
        else Graphics.leapInActive

      val scene =
        SceneUpdateFragment(
          Layer(viewModel.bg.clones)
            .withMagnification(3)
            .withBlendMaterial(blendMaterial)
            .withLights(lights),
          Layer(holes :+ exit)
            .withMagnification(3)
            .withBlendMaterial(blendMaterial)
            .withLights(lights),
          Layer(
            moveHighlights ++ hover
          )
            .withMagnification(3)
            .withBlendMaterial(blendMaterial),
          Layer(
            npcs ++
              player
          )
            .withMagnification(3)
            .withBlendMaterial(blendMaterial)
            .withLights(lights),
          Layer(
            Graphics.scroll,
            leapIcon
          )
            .withMagnification(3),
          Layer(statusText)
            .withMagnification(1)
        ).addCloneBlanks(viewModel.bg.blanks)

      Outcome(scene)
        .addGlobalEvents(
          if mouseOverLeapButton && model.leapAvailable && context.mouse.mouseClicked then
            Batch(GameEvent.ToggleLeapMode)
          else Batch.empty
        )

object Graphics:

  val shadow =
    Shape
      .Circle(Circle(0, 0, 5), Fill.Color(RGBA.Black.withAlpha(0.35)))
      .withScale(Vector2(1.0, 0.4))

  def makeGraphic(char: Int): Graphic[TerminalMaterial] =
    Graphic(
      16,
      16,
      TerminalMaterial(Assets.assets.tilesheet, RGBA.White, RGBA.Zero)
    )
      .withCrop(
        Rectangle(
          RogueTerminalEmulator.indexToPoint(char, 16) * ViewModel.squareSize,
          Size(16)
        )
      )

  val yellowHighlight =
    makeGraphic(Tile.LATIN_CAPITAL_LETTER_C_WITH_CEDILLA.toInt)

  val orangeHighlight =
    makeGraphic(Tile.LATIN_SMALL_LETTER_U_WITH_DIAERESIS.toInt)

  val cyanHighlight =
    makeGraphic(Tile.LATIN_SMALL_LETTER_E_WITH_ACUTE.toInt)

  val scrollLeft =
    makeGraphic(Tile.LATIN_CAPITAL_LETTER_E_WITH_ACUTE.toInt)

  val scrollMiddle =
    makeGraphic(Tile.LATIN_SMALL_LETTER_AE.toInt)

  val scrollRight =
    makeGraphic(Tile.LATIN_CAPITAL_LETTER_AE.toInt)

  val leapInActive =
    makeGraphic(Tile.LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX.toInt).moveTo(Point(8 * 16, 10 * 16))

  val leapActive =
    makeGraphic(Tile.LATIN_SMALL_LETTER_O_WITH_DIAERESIS.toInt).moveTo(Point(8 * 16, 10 * 16))

  val leapUnavailable =
    makeGraphic(Tile.LATIN_SMALL_LETTER_O_WITH_GRAVE.toInt).moveTo(Point(8 * 16, 10 * 16))

  val scroll: Group =
    Group(
      Batch(
        scrollLeft,
        scrollMiddle.moveTo(Point(1 * 16, 0)),
        scrollMiddle.moveTo(Point(2 * 16, 0)),
        scrollMiddle.moveTo(Point(3 * 16, 0)),
        scrollMiddle.moveTo(Point(4 * 16, 0)),
        scrollMiddle.moveTo(Point(5 * 16, 0)),
        scrollMiddle.moveTo(Point(6 * 16, 0)),
        scrollMiddle.moveTo(Point(7 * 16, 0)),
        scrollMiddle.moveTo(Point(8 * 16, 0)),
        scrollRight.moveTo(Point(9 * 16, 0))
      )
    ).moveTo(0, 10 * 16)

  val blankGraphic =
    makeGraphic(Tile.SPACE.toInt)

  val playerGraphic =
    makeGraphic(Tile.COMMERCIAL_AT.toInt)
      .withRef(0, 4)
      .modifyMaterial(
        _.withLighting(
          LightingModel.Lit.flat
            .withEmissive(Assets.assets.tilesheetEmissive, 1.0)
            .withNormal(Assets.assets.tilesheetNormal, 1.0)
        )
      )

  val enemyGraphic =
    makeGraphic(Tile.WHITE_SMILING_FACE.toInt)
      .withRef(0, 1)
      .modifyMaterial(
        _.withLighting(
          LightingModel.Lit.flat
            .withEmissive(Assets.assets.tilesheetEmissive, 1.0)
            .withNormal(Assets.assets.tilesheetNormal, 1.0)
        )
      )

  val enemyDeadGraphic =
    makeGraphic(Tile.BLACK_SMILING_FACE.toInt)
      .modifyMaterial(
        _.withLighting(
          LightingModel.Lit.flat
            .withEmissive(Assets.assets.tilesheetEmissive, 1.0)
            .withNormal(Assets.assets.tilesheetNormal, 1.0)
        )
      )

  val exitGraphic =
    makeGraphic(Tile.BLACK_DOWN_POINTING_TRIANGLE.toInt)
      .modifyMaterial(
        _.withLighting(
          LightingModel.Lit.flat
            .withEmissive(Assets.assets.tilesheetEmissive, 1.0)
            .withNormal(Assets.assets.tilesheetNormal, 1.0)
        )
      )

  val holeBottomGraphic =
    makeGraphic(Tile.LATIN_SMALL_LETTER_A_WITH_ACUTE.toInt)
      .modifyMaterial(
        _.withLighting(
          LightingModel.Lit.flat
            .withEmissive(Assets.assets.tilesheetEmissive, 1.0)
            .withNormal(Assets.assets.tilesheetNormal, 1.0)
        )
      )

  val holeMiddleGraphic =
    makeGraphic(Tile.LATIN_SMALL_LETTER_I_WITH_ACUTE.toInt)
      .modifyMaterial(
        _.withLighting(
          LightingModel.Lit.flat
            .withEmissive(Assets.assets.tilesheetEmissive, 1.0)
            .withNormal(Assets.assets.tilesheetNormal, 1.0)
        )
      )

  val holeTopGraphic =
    makeGraphic(Tile.LATIN_SMALL_LETTER_O_WITH_ACUTE.toInt)
      .modifyMaterial(
        _.withLighting(
          LightingModel.Lit.flat
            .withEmissive(Assets.assets.tilesheetEmissive, 1.0)
            .withNormal(Assets.assets.tilesheetNormal, 1.0)
        )
      )

  val rainbowBox =
    BlankEntity(Size(16, 16), RainbowShader.shaderData)

