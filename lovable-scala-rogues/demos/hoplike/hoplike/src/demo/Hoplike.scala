package demo

import indigo.*
import indigo.syntax.*
import roguelikestarterkit.*
import roguelikestarterkit.syntax.*
import demo.generated.*

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Hoplike extends IndigoDemo[Size, Size, Model, ViewModel]:

  val eventFilters: EventFilters = EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[Size]] =
    Outcome(
      BootResult(Config.config.withMagnification(1), Config.config.viewport.size / 2)
        .withAssets(Assets.assets.assetSet)
        .withShaders(
          roguelikestarterkit.shaders.all ++
            Set(RainbowShader.shader)
        )
        .withFonts(RoguelikeTiles.Size16x16.Fonts.fontInfo)
    )

  def initialModel(startupData: Size): Outcome[Model] =
    Outcome(Model.initial(Size(10))).addGlobalEvents(GameEvent.LoadGame)

  def initialViewModel(startupData: Size, model: Model): Outcome[ViewModel] =
    Outcome(ViewModel.empty)

  def setup(bootData: Size, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Size]] =
    Outcome(Startup.Success(bootData))

  def updateModel(context: FrameContext[Size], model: Model): GlobalEvent => Outcome[Model] =
    e => Model.updateModel(context, model)(e)

  def updateViewModel(
      context: FrameContext[Size],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    e => ViewModel.updateViewModel(context, model, viewModel)(e)

  def present(
      context: FrameContext[Size],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    View.present(context, model, viewModel)
