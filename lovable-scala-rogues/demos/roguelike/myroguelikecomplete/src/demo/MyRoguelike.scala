package demo

import indigo.*
import indigo.scenes.*
import roguelikestarterkit.*
import demo.generated.*
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object MyRoguelike extends IndigoSandbox[Unit, Model]:

  val animations: Set[Animation]  = Set()
  val assets: Set[AssetType]      = Assets.assets.assetSet
  val config: GameConfig          = Config.config.withMagnification(3)
  val fonts: Set[FontInfo]        = Set()
  val shaders: Set[indigo.Shader] = roguelikestarterkit.shaders.all

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def updateModel(context: FrameContext[Unit], model: Model): GlobalEvent => Outcome[Model] =
    case KeyboardEvent.KeyDown(Key.UP_ARROW) =>
      model.bump(Point(0, -1))

    case KeyboardEvent.KeyDown(Key.DOWN_ARROW) =>
      model.bump(Point(0, 1))

    case KeyboardEvent.KeyDown(Key.LEFT_ARROW) =>
      model.bump(Point(-1, 0))

    case KeyboardEvent.KeyDown(Key.RIGHT_ARROW) =>
      model.bump(Point(1, 0))

    case _ =>
      Outcome(model)

  def present(context: FrameContext[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val terminal: RogueTerminalEmulator =
      RogueTerminalEmulator(Size(16, 16))
        .fill(MapTile(Tile.DARK_SHADE, RGBA.Red, RGBA.Black))
        .fillRectangle(model.floor, MapTile(Tile.LIGHT_SHADE, RGBA.Red.mix(RGBA.Black), RGBA.Black))
        .put(model.player, Tile.`@`, RGBA.Cyan, RGBA.Zero)

    val tiles =
      terminal.toCloneTiles(CloneId("demo"), Point.zero, RoguelikeTiles.Size10x10.charCrops) { (fg, bg) =>
        Graphic(10, 10, TerminalMaterial(Assets.assets.AnikkiSquare10x10, fg, bg))
      }

    Outcome(
      SceneUpdateFragment(
        Layer(
          tiles.clones
        )
      ).addCloneBlanks(tiles.blanks)
    )

final case class Model(player: Point, room: Rectangle):
  val floor: Rectangle = room.contract(1)

  def bump(next: Point): Outcome[Model] =
    if floor.contains(player + next) then Outcome(this.copy(player = player + next))
    else Outcome(this)

object Model:
  val initial: Model =
    Model(Point(3), Rectangle(16, 16))
