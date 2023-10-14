

What am I doing?

Aiming for inspirational over academic.
Show people they can do it too, that they have transferable skills.
I'm like people leaving thinking - I should give that a try!

The best talks I know start at a level everyone can get, and end slightly beyond the audience giving them something new to want to find out / talk about.

Logos - Logical.
Tell a good story. Make sure it flows.

Ethos - We're all in this together.
I'm a pretty ordinary dev, a generalist, my main virtues are stubborness and weaponised ignorance.

Pathos - Funny.
Doesn't mean jokes, necessarily, it just means delivered with warmth and enthusiasm, I think.

---

This is the story of the roguelike
How did I get into this mess?
The first problem was just rendering it.
Doing the tutorial
Turning it into a demo, the levels suck.
The work continues, you should do this too.

---

# Context: I built a roguelike! (2 minutes)
What is Indigo
Here is my roguelike (Play it)
How did I end up building a roguelike?

## What is a roguelike? (Very accessible, bit niche, good fun.) (8 minutes)
- Rogue - the original
- Dwarf fortress - most famous?
- Cogmind - Gorgeous.
- Hoplite - try it! Accessible.

Roguelike-Dev follow along.
- Python tutorial
  - Mutable state
  - Imperative programming
  - prints straight to the terminal
- Great fun! You should do it too!

Going to look at two problems that came out of this:
1. Rendering ASCII art
2. Dungeon layouts

# Rendering a Roguelike (15 minutes)

Enabling people to build a roguelike in Indigo, we're missing some tools.

The first problem was just rendering it. Python version just renders to console.
Indigo not designed for this.

Show ASCII here, but latest version uses a very similar approach with nicer graphics.

Dwarf fortress assets
- What are they?
- How the grid correlates to key codes

Loading the asset
Drawing one char (Graphics)
Drawing many chars (TileMap + CloneTiles)
Transparency (Custom Shader)
Colours (Extend the Shader)

This was all eventually wrapped up in the Roguelike-Starterkit.
The terminal emulator
- Provides a nice abstraction
- How it fits into the Indigo life cycle

# Then I did the tutorial (3 minutes)

I've come this far... might as well do the follow-along?

Took ages
learnt a lot about game dev
Because it's imperative python not FP scala, it was more like interpretive dance
Completed, it's in a repo, the code is terrible.

# Fixing the dungeon layout (15 minutes)

Yeah, the code is bad... but there's a demo here!
Cleaned up the code and gave it nicer graphics.
Breaks a cardinal sin of game dev (for me) - too much walking!
Need better levels.

Build a dungeon viewer so I could see what was going on.

How does it work now?
- The algo
- The good - nice and easy, pretty interesting looking result, always works!
- The bad - weird layouts, massive corridors

Fixing it
- Starting with the rooms
- But now they aren't naturally connected
- Point cloud
- Delaunay Triangulation
- Now we have relationships, but we need corridors
- Adjustments and connections

# Wrap up (2 minutes)
During this experience, I've ended up rather enjoying roguelikes.
Have a go!
Maybe this looks a long way away from your day jobs, but it's more accessible that you think!
Links to stuff
