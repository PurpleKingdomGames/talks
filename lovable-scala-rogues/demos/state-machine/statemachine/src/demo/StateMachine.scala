package demo

import indigo.*
import indigo.scenes.*
import scala.scalajs.js.annotation.JSExportTopLevel

import demo.generated.Config
import demo.generated.Assets

@JSExportTopLevel("IndigoGame")
object StateMachine extends IndigoSandbox[Unit, Model]:

  val config: GameConfig         = Config.config.withMagnification(6)
  val assets: Set[AssetType]     = Assets.assets.assetSet
  val fonts: Set[FontInfo]       = Set()
  val animations: Set[Animation] = Set()
  val shaders: Set[Shader]       = Set()

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model(TrafficLights.Red))

  def setup(
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def updateModel(
      context: FrameContext[Unit],
      model: Model
  ): GlobalEvent => Outcome[Model] =
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      // Using a spacebar press in place of some complicated logic
      // that results in the need to move to the next state.
      Outcome(model).addGlobalEvents(GameEvent.NextState)

    case GameEvent.NextState =>
      Outcome(model.next)

    case _ =>
      Outcome(model)

  def present(
      context: FrameContext[Unit],
      model: Model
  ): Outcome[SceneUpdateFragment] =
    val crop =
      model.state match
        case TrafficLights.Red   => Rectangle(0, 0, 64, 64)
        case TrafficLights.Amber => Rectangle(64, 0, 64, 64)
        case TrafficLights.Green => Rectangle(0, 64, 64, 64)

    Outcome(
      SceneUpdateFragment(
        Graphic(64, 64, Assets.assets.trafficlightsMaterial)
          .withCrop(crop)
      )
    )

final case class Model(state: TrafficLights):
  def next: Model =
    this.copy(state = state.next)

enum TrafficLights:
  case Red, Amber, Green

  def next: TrafficLights =
    this match
      case TrafficLights.Red   => TrafficLights.Amber
      case TrafficLights.Amber => TrafficLights.Green
      case TrafficLights.Green => TrafficLights.Red

enum GameEvent extends GlobalEvent:
  case NextState
