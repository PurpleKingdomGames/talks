
# Losing is Fun! (1 minutes)
Hi, I'm Dave, and I build a game engine for Scala called Indigo.
One day, someone turned up on Discord and asked: "Can I use Indigo to build a roguelike?" and I said "What's a roguelike?"

## What is a roguelike? (4 minutes)
For the uninitiated:
Traditionally, a turn based ASCII art game.
Strapline is "Losing is fun" - think chess.
- Rogue - the original
- Dwarf fortress - most famous?
- Cogmind - Gorgeous.
- Hoplite - try it! Accessible. This is where I really "got it"

Set out on a mission to do the roguelikedev follow along in Scala with Indigo.

- Python tutorial
  - Mutable state
  - Imperative programming
  - prints straight to the terminal
- Low pressure
- Everyone share's their work
- Some people do this every year and just experiment with different ways to build roguelikes.
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
Interlude: Performance
Drawing many chars (CloneTiles)
Transparency (Custom Shader)
Colours (Extend the Shader)

This was all eventually wrapped up in the Roguelike-Starterkit.
The terminal emulator
- Provides a nice abstraction
- How it fits into the Indigo life cycle

# Then I did the tutorial (4 minutes)

I've come this far... might as well do the follow-along?

Took ages, learnt a lot about game dev
Would highly recommend
Because it's imperative python not FP scala, it was more like interpretive dance
Completed, it's in a repo, the code is terrible.
...but it's fun! There is a game here! 
...and maybe there's an Indigo demo, with work. So I polished it up, cleaned up the code, and we got this (show current version)

# Fixing the dungeon layout (20 minutes)

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
- Interlude: Abstractions to the rescue.
- Delaunay Triangulation
- Now we have relationships, but we need corridors
- Adjustments and connections

# Wrap up (1 minutes)
During this experience, I've ended up rather enjoying roguelikes.
Have a go!
Maybe this looks a long way away from your day jobs, but it's more accessible that you think!
Links to stuff
