#!/bin/bash

# Revealjs options are exposed as pandoc variables, e.g.: -V controls=true \

pandoc -t revealjs \
  -s -o index.html Talk.md \
  -V revealjs-url=https://unpkg.com/reveal.js/ \
  --include-in-header=style.html \
  -V theme=dracula \
  -V controls=true \
  --self-contained

