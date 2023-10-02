Titles?

- How to Build a Dungeon
- Beginners Guide to Dungeon Design
- The Homeowners Guide to Private Dungeon Construction
- Dungeon Building 101
- Delaunay's Dungeons
- All of the Dungeons, None of the Walking
- Putting the Funtional into Fungeons.


Abstract:


Noel keeps asking if someone can do a talk, anything work relevant. Unfortunately, I don't really do work relevant, so I've offered to do the Christmas talk, which is a bit more of a jolly.

I'm Dave, I look after a few open source projects, Indigo, Tyrian and Ultraviolet.

Indigo is a game engine...
Haven't done a talk on it since...
Thought is was about time.

I don't do academic or work related talks, this is just something I was working on that was kinda fun and I think interesting.

Back in ... someone said to me "Can you using Indigo to make a roguelike" "What's a roguelike?"

---

What is a roguelike?

---

The Roguelike tutorials

Even year, r/RoguelikeDev runs a follow along (hosted by the cogmind guy) of the official roguelike tutorials, and I decided to have a go.

Took me ages, but it's one of the best things I've done.

Spawned Indigo's only official extension, the roguelike-starterkit, which I've used many times now for different things.

Code is awful, here's the repo.

It was more interpretation than direct follow along...
architecture differences...
Some problems for them weren't problems for me...

Having completed that... now what?

---

A New Demo

Wanted to use the final version as a new engine demo.

The final code was bad, and if I wasn't frantically trying to keep up I would have done lots of things differently, now was my chance to do that.

Rendering....

In the end it looked like this.

Assets are paid for, code is here. Doing this has spawned _many_ engine improvements, such as... nothing like dog fooding.

---

The biggest sin in video game design

...in my opinion, is making people to long boring walks with nothing to do.

And unfortunately, that's what this implementation does. Lets look at some maps.

They're kinda interesting looking! Massive corridors though and pretty random.

How can we improve the situation?

---

Dungeon viewer

---

How it works now

Great for a tutorial because it's dead simple and works surprisingly well.

---

But we want short corridors... I found a tutorial.

I tend not to read tutorials, I skim them for concepts and reinvent in order to learn things.

1. Delaunay Triangulation
2. This gif.

---

Interlude: Reinventing the wheel.

---

Interlude: Performance is relative.

---

How to delaunay
 (Things built into Indigo, I don't care about performance.)

---

Ok we've got it... now what?


