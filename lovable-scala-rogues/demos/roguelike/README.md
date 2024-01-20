# My Roguelike

## Getting started with Mill

The repo contains a version of the [millw](https://github.com/lefou/millw) launch script, so even if you have Mill installed globably, you're advised to use run Mill as `./mill <commands>`.

Below is a list of useful commands, including some generally useful ones in case you're new to Mill.

## Quick command aliases

These are defined in the `build.sc` file in this project, they are a combination of other built-in commands

```bash
# Run your game via Electron using fast compilation
./mill myroguelike.runGame

# Run your game via Electron using full compilation and compression
./mill myroguelike.runGameFull

# Build your game as a static website using fast compilation
./mill myroguelike.buildGame

# Build your game as a static website using full compilation and compression
./mill myroguelike.buildGameFull
```

## Basic Mill commands

```bash
# Compile everything
./mill __.compile

# Clean the game project
./mill clean myroguelike

# Compile the game
./mill myroguelike.compile

# Run your game modules tests
./mill myroguelike.test
```

## Scala.js commands

```bash
# Scala.js fast compile (large file size)
./mill myroguelike.fastLinkJS

# Scala.js full compile and compress
./mill myroguelike.fullLinkJS
```

## Indigo commands

```bash
# Build your game as a static website using fast compilation, assumes you have already compiled to Scala.js using fastLinkJS
./mill myroguelike.indigoBuild

# Build your game as a static website using full compilation and compression, assumes you have already compiled to Scala.js using fullLinkJS
./mill myroguelike.indigoBuildFull

# Run your game via Electron using fast compilation, assumes you have already compiled to Scala.js using fastLinkJS
./mill myroguelike.indigoRun

# Run your game via Electron using full compilation and compression, assumes you have already compiled to Scala.js using fullLinkJS
./mill myroguelike.indigoRunFull
```
