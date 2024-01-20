package demo

import indigo.*

import scala.scalajs.js.annotation.*
import generated.*

@JSExportTopLevel("IndigoGame")
object Rainbow extends IndigoShader:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType]      = Set()
  val channel0: Option[AssetPath] = None
  val channel1: Option[AssetPath] = None
  val channel2: Option[AssetPath] = None
  val channel3: Option[AssetPath] = None

  val shader: Shader =
    CustomShader.shader

object CustomShader:

  val shader: Shader =
    UltravioletShader.entityFragment(
      ShaderId("shader"),
      EntityShader.fragment[FragmentEnv](fragment, FragmentEnv.reference)
    )

  import ultraviolet.syntax.*

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def fragment: Shader[FragmentEnv, Unit] =
    Shader[FragmentEnv] { env =>

      def sdfCircle(p: vec2, r: Float): Float =
        length(p) - r

      // Takes a value p from 0 to 1 and returns the colour from the rainbow
      def rainbow(p: Float): vec4 =
        val interval = 1.0f / 7.0f

        val redAmount    = 1.0f - step(interval * 1.0f, p)
        val orangeAmount = 1.0f - step(interval * 2.0f, p)
        val yellowAmount = 1.0f - step(interval * 3.0f, p)
        val greenAmount  = 1.0f - step(interval * 4.0f, p)
        val blueAmount   = 1.0f - step(interval * 5.0f, p)
        val cyanAmount   = 1.0f - step(interval * 6.0f, p)

        val red    = vec4(1.0f, 0.0f, 0.0f, 1.0f)
        val orange = vec4(1.0f, 0.5f, 0.0f, 1.0f)
        val yellow = vec4(1.0f, 1.0f, 0.2f, 1.0f)
        val green  = vec4(0.0f, 1.0f, 0.0f, 1.0f)
        val blue   = vec4(0.0f, 1.0f, 1.0f, 1.0f) // Cyan
        val cyan   = vec4(0.5f, 0.0f, 1.0f, 1.0f) // Purple
        val purple = vec4(1.0f, 0.2f, 0.8f, 1.0f) // Pink

        mix(
          purple,
          mix(
            cyan,
            mix(
              blue,
              mix(
                green,
                mix(
                  yellow,
                  mix(orange, red, redAmount),
                  orangeAmount
                ),
                yellowAmount
              ),
              greenAmount
            ),
            blueAmount
          ),
          cyanAmount
        )

      def linear(edge0: Float, edge1: Float, x: Float): Float =
        clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f)

      def cloud(uv: vec2, p: vec2, radius: Float): Float =
        val distance     = length(uv - p)
        val blobStrength = 10.0f
        val influence =
          blobStrength * exp(-distance * distance / (2.0f * radius * radius))

        influence

      def allClouds: vec4 =
        val uv: vec2 = (2.0f * env.SCREEN_COORDS - env.SIZE) / env.SIZE.y

        val c1 = cloud(uv, vec2(-0.75f, -0.75f), 0.1f)
        val c2 = cloud(uv, vec2(-0.95f, -0.7f), 0.065f)
        val c3 = cloud(uv, vec2(-0.55f, -0.7f), 0.07f)
        val c4 = cloud(uv, vec2(-0.25f, -0.72f), 0.04f)
        val c5 = cloud(uv, vec2(-0.15f, -0.705f), 0.025f)
        val c6 = cloud(uv, vec2(0.8f, -0.9f), 0.15f)
        val c7 = cloud(uv, vec2(0.4f, -0.95f), 0.1f)

        val acc: Float =
          clamp((c1 + c2 + c3 + c4 + c5 + c6 + c7) / 7.0f, 0.0f, 1.0f)

        vec4(acc)

      def fragment(color: vec4): vec4 =

        // waves

        val offset: Float = 0.25f + (sin(env.UV.x * env.SIZE.x / 8.0f) * 0.01f)
        val yPos: Float   = env.UV.y + offset
        val waveAmount: Float = step(
          0.5f,
          0.5f + sin(yPos * env.SIZE.x / (50.0f * (env.UV.y - 0.5f)))
        )

        // rainbow

        val rainbowUV =
          if env.UV.y < 0.5f then env.UV
          else vec2(env.UV.x * (1.0f + (waveAmount * 0.015f)), env.UV.y)

        val sdf = sdfCircle(rainbowUV - 0.5f, 0.25f)

        val amount =
          1.0f - linear(0.83f, 1.0f, 1.0f - abs(sdf)) // magic numbers...

        val col = rainbow(amount) // vec4(0.3f) + (rainbow(amount) * 0.7f)

        val innerMask =
          step(0.0f, sdfCircle(rainbowUV - 0.5f, 0.08f)) // Magic numbers...
        val outerMask = 1.0f - step(0.0f, sdf)
        val mask      = (innerMask + outerMask) - 1.0f
        val alpha     = if rainbowUV.y > 0.5f then mask * 0.3f else mask

        val theRainbow = vec4(col.rgb, alpha)

        // clouds

        val clouds = allClouds

        // bg

        val bgGradient = 1.0f - sqrt(abs(env.UV.y - 0.5f))

        val skyBlue = vec4(0.329f, 0.768f, 1.0f, 1.0f)
        val seaBlue =
          vec4(0.0f, 0.392f, 0.854f, 1.0f) * (0.8f + waveAmount * 0.2f)
        val bgColour = mix(skyBlue, seaBlue, step(0.5f, env.UV.y))

        val bgBase = vec4(bgColour.rgb + bgGradient, 1.0f)
        val bg     = bgBase + allClouds // mix(bgBase, clouds, clouds.a)

        // Composited

        vec4(mix(bg.rgb, theRainbow.rgb, alpha), 1.0f)

    }
