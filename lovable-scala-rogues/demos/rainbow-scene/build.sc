import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`
import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import $ivy.`io.indigoengine::mill-indigo:0.16.0`, millindigo._

object rainbow extends ScalaJSModule with MillIndigo {
  def scalaVersion   = "3.3.1"
  def scalaJSVersion = "1.15.0"

  val indigoVersion = "0.16.0"

  val indigoOptions: IndigoOptions =
    IndigoOptions.defaults
      .withTitle("Rainbow")
      .withWindowSize(800, 800)
      .withBackgroundColor("black")
      .withAssetDirectory(os.RelPath.rel / "assets")
      .excludeAssets {
        case p if p.endsWith(os.RelPath.rel / ".gitkeep") => true
        case _                                            => false
      }

  val indigoGenerators: IndigoGenerators =
    IndigoGenerators("demo.generated")
      .generateConfig("Config", indigoOptions)

  def ivyDeps =
    Agg(
      ivy"io.indigoengine::indigo-json-circe::$indigoVersion",
      ivy"io.indigoengine::indigo::$indigoVersion",
      ivy"io.indigoengine::indigo-extras::$indigoVersion"
    )

  object test extends ScalaJSTests {
    def ivyDeps = Agg(
      ivy"org.scalameta::munit::0.7.29"
    )

    def testFramework = "munit.Framework"

    override def moduleKind  = T(mill.scalajslib.api.ModuleKind.CommonJSModule)
  }

  def buildGame() =
    T.command {
      T {
        compile()
        fastLinkJS()
        indigoBuild()()
      }
    }

  def buildGameFull() =
    T.command {
      T {
        compile()
        fullLinkJS()
        indigoBuildFull()()
      }
    }

  def runGame() =
    T.command {
      T {
        compile()
        fastLinkJS()
        indigoRun()()
      }
    }

  def runGameFull() =
    T.command {
      T {
        compile()
        fullLinkJS()
        indigoRunFull()()
      }
    }

}
