
## Shaders 101
Drawing with maths in an embarrassingly parallel world.

---

Slide 2

```scala
def foo: Int = ???
```

---

```glsl
void fragment() {
  float r = UV.x * (1.0 + sin(TIME) / 2.0);
  float g = UV.y * (1.0 + cos(TIME) / 2.0);
  COLOR = vec4(r, g, 0.0, 1.0);
}
```

```glsl
layout (std140) uniform FireData {
  float OFFSET;
  vec3 COLOR_OUTER;
  vec3 COLOR_INNER;
  vec3 COLOR_CENTER;
};
```
---


Notes

- Part 1 (5 mins) - Going to talk about shader, but a quick "How did I end up here?"
	- Flash dev (so much fun, lots of creativity)
	- Moved to scala (great at testing, great at programs you can reason about and understand)
	- Wouldn't it nice if Scala was as much fun as Flash? Can't go back to flash but miss those days. Indigo was in Nov 2016.
	- Indigo now has shader
- Qualifications to talk about shaders
	- X Mathemathican
	- X Shader Wizard
	- Enthusiastic amateur generalist :-)

Meet Bob.

- Part 2 (5 mins) - Computer graphics pipelines primer
	- This is all going to be a bit high level and hand wavey - just want to give you some intuition.
	- Most people know that computer graphics are made of triangles (which is mostly true)
		- Why triangles? They only ever exist on one plane - three legged stool can't wobble.
	- A graphic pipeline basically:
		- Takes a scene
		- Converts it into triangles
		- Colors in the triangles
		- Presents the frame to the player.
	- Shaders in WebGL are programs comprised of two parts (a nice illustration / animation)
		- The vertex shader - where does the triangle go?
		- The fragment shader - what color are it's pixels?
	- Vertex shaders
		- World space to screen space
		- Data Interpolation
	- Fragment shaders
		- Where really only going to talk about fragment shaders from now on.
		- Knows which pixel it's drawing
		- Decides what color that pixel needs to be.
		- A whole program is executed for every pixel on the screen, so it's gotta be quick.

- Part 3 (5 mins) - Embarrassing Parallelism

The title of the talk mentions embarassing parallelism... so I'd better explain that.

> "Parallel computing is a type of computation in which many calculations or processes are carried out simultaneously."

And it's reputedly very hard.
Unless you're a functional programmer...
Immutable data.
Shaders are like functional programs. Kinda.

**Context here is really important, this stuff only works where each pixel is treated in isolation. You can't see your neighbours and as them what colour they're _going_ to be.

This is a good start, but we also need dedicated hardware.

CPU vs GPU.

And it's called embarassing parallelism because this is supposed to be hard, but GPU's make it look easy.

- Part 4 (5 mins) - Couple of quick examples of shader programs

Before we get into anything complicated, I just want to show you what a fragment shader program looks like.

We have two triangles that make a rectanglular space on the screen.

Data types
c programs
limited

	- Example 1: Green flood fill
	- Example 2: UVs

Back to bob! Oh dear... bob's frozen

- Part 3 - Lets start a fire for Bob

Ok so this is going to be stylised fire - Bob is a pixel art character so nothing too realistic.

We're going to try and make something that looks more like _this_ and less like _this._

So the plan is quite simple. What we want to do, for each of the colors, is draw a circle and then distort it into a flame shape that animates over time.

So lets start with a circle.

We're going to use something called an SDF, which stands for Signed Distance Function.

There are lots of different SDF functions for anything you can think of. They're one of the cornerstones of graphics programming be it 2D or 3D.

Here is a scene by Inigo Quillez, all done with SDFs.

Here is the SDF for our circle.

`length(position) - radius`

And what you get is a distance to the edge of the circle that is centered around the origin.

Here's a version in GLSL, and here is the result.

Hmm... Bit boring and hard to see (gradiant)

Trouble is that the value we're getting isn't very useful by itself, but you can do interesting things with it.

Here is that value being passed through a cos function for example.

But we we want is a step, which on a graph looks like this:

It takes our value and if it passed a threshold - 0 in our case - flips from 0.0 to 1.0, and here is our circle.

Ok.

Distortion.

So to distort the circle, we're going to need something to distort it by...

- Noise

We're going to use procedural noise.

Probably the most famouse noise type is Perlin Noise
Created by Ken Perlin. Ken was working the movie Tron and was asked to make the textures more interesting.

Noise is complicated - at least for me, maths it's my strong suit - but here's a quick look at roughly how it works.

The way it works basically:
1. Define a grid of vectors - has to be predicatable / psuedorandom - done with signal based randomness base on position.
2. Find the dot product of each point to it's closest gradient vectors (corners)
3. Interpolate between the values

Here is what our noise looks like.

Two problems:
1. It's not obviously flame-like
2. It's not moving, and we're going to want our flames to be animated.

We can animate by accounting for Time, here it is.

This is the running time in decimal seconds for frame independance.

...

So here they are overlayed, noise and circle.

To do the distoration, recall that the sdf is based on a point, and that point is the UV... but we can move it! So what we'll do, is take the point and move it an amount based on the noise we've generated.

We're getting somewhere, looks terrible.

Now we need to impose H and V limits. To do that we're going to manipulate our UVs.

This allows us to control how much the positions is influenced.

And now we have fire.

Give it some color

To make the inner rings, all we have to do is run the exact same function again, but with smaler circles that are offset a bit.



Help bob.



#itv
#talks #scala #shaders #indigo