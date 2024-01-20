# State Machine

## Getting started with Mill

The repo contains a version of the [millw](https://github.com/lefou/millw) launch script, so even if you have Mill installed globably, you're advised to use run Mill as `./mill <commands>`.

Below is a list of useful commands, including some generally useful ones in case you're new to Mill.

> Note that if you're using zsh, you may need to surround argument groups with single quotes, if you have special characters like hypens in your project name.

## Quick command aliases

These are defined in the `build.sc` file in this project, they are a combination of other built-in commands

```bash
# Run your game via Electron using fast compilation
./mill basic.runGame

# Run your game via Electron using full compilation and compression
./mill basic.runGameFull

# Build your game as a static website using fast compilation
./mill basic.buildGame

# Build your game as a static website using full compilation and compression
./mill basic.buildGameFull
```

## Basic Mill commands

```bash
# Compile everything
./mill __.compile

# Clean the game project
./mill clean basic

# Compile the game
./mill basic.compile

# Run your game modules tests
./mill basic.test
```

## Scala.js commands

```bash
# Scala.js fast compile (large file size)
./mill basic.fastLinkJS

# Scala.js full compile and compress
./mill basic.fullLinkJS
```

## Indigo commands

```bash
# Build your game as a static website using fast compilation, assumes you have already compiled to Scala.js using fastLinkJS
./mill basic.indigoBuild

# Build your game as a static website using full compilation and compression, assumes you have already compiled to Scala.js using fullLinkJS
./mill basic.indigoBuildFull

# Run your game via Electron using fast compilation, assumes you have already compiled to Scala.js using fastLinkJS
./mill basic.indigoRun

# Run your game via Electron using full compilation and compression, assumes you have already compiled to Scala.js using fullLinkJS
./mill basic.indigoRunFull
```
