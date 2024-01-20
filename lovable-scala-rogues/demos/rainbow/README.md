# State Machine

## Getting started with Mill

The repo contains a version of the [millw](https://github.com/lefou/millw) launch script, so even if you have Mill installed globably, you're advised to use run Mill as `./mill <commands>`.

Below is a list of useful commands, including some generally useful ones in case you're new to Mill.

> Note that if you're using zsh, you may need to surround argument groups with single quotes, if you have special characters like hypens in your project name.

## Quick command aliases

These are defined in the `build.sc` file in this project, they are a combination of other built-in commands

```bash
# Run your game via Electron using fast compilation
./mill rainbow.runGame

# Run your game via Electron using full compilation and compression
./mill rainbow.runGameFull

# Build your game as a static website using fast compilation
./mill rainbow.buildGame

# Build your game as a static website using full compilation and compression
./mill rainbow.buildGameFull
```

## Basic Mill commands

```bash
# Compile everything
./mill __.compile

# Clean the game project
./mill clean rainbow

# Compile the game
./mill rainbow.compile

# Run your game modules tests
./mill rainbow.test
```

## Scala.js commands

```bash
# Scala.js fast compile (large file size)
./mill rainbow.fastLinkJS

# Scala.js full compile and compress
./mill rainbow.fullLinkJS
```

## Indigo commands

```bash
# Build your game as a static website using fast compilation, assumes you have already compiled to Scala.js using fastLinkJS
./mill rainbow.indigoBuild

# Build your game as a static website using full compilation and compression, assumes you have already compiled to Scala.js using fullLinkJS
./mill rainbow.indigoBuildFull

# Run your game via Electron using fast compilation, assumes you have already compiled to Scala.js using fastLinkJS
./mill rainbow.indigoRun

# Run your game via Electron using full compilation and compression, assumes you have already compiled to Scala.js using fullLinkJS
./mill rainbow.indigoRunFull
```
