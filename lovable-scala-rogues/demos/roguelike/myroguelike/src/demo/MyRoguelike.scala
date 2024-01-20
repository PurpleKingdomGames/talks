package demo

import indigo.*
import indigo.scenes.*
import roguelikestarterkit.*
import demo.generated.*
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object MyRoguelike extends IndigoSandbox[Unit, Unit]:

  val animations: Set[Animation]  = Set()
  val assets: Set[AssetType]      = Assets.assets.assetSet
  val config: GameConfig          = Config.config.withMagnification(3)
  val fonts: Set[FontInfo]        = Set()
  val shaders: Set[indigo.Shader] = roguelikestarterkit.shaders.all

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def updateModel(context: FrameContext[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def present(context: FrameContext[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    val terminal: RogueTerminalEmulator =
      RogueTerminalEmulator(Size(16, 16))
        .putLine(Point.zero, "Hello, world!", RGBA.White, RGBA.Zero)
      // .fill(MapTile(Tile.DARK_SHADE, RGBA.Red, RGBA.Black))
      // .fillRectangle(Rectangle(1, 1, 14, 14), MapTile(Tile.LIGHT_SHADE, RGBA.Red.mix(RGBA.Black), RGBA.Black))
      // .put(Point(3), Tile.`@`, RGBA.Cyan, RGBA.Zero)

    val terminalClones =
      terminal.toCloneTiles(CloneId("demo"), Point.zero, RoguelikeTiles.Size10x10.charCrops) { (fg, bg) =>
        Graphic(10, 10, TerminalMaterial(Assets.assets.AnikkiSquare10x10, fg, bg))
      }

    Outcome(
      SceneUpdateFragment(
        Layer(
          terminalClones.clones
        )
      ).addCloneBlanks(terminalClones.blanks)
    )
