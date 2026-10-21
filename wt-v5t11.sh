#!/usr/bin/env bash

DIR="${1:-$(pwd)}"

wt.exe -w new \
  new-tab --title "v5t11" \
    wsl.exe -d Ubuntu --cd "$DIR/v5t11-status" \; \
  split-pane -H --size 0.5 \
    wsl.exe -d Ubuntu --cd "$DIR/v5t11-fahrzeuge" \; \
  move-focus down \; \
  split-pane -H --size 0.5 \
    wsl.exe -d Ubuntu --cd "$DIR/v5t11-stellwerk" \; \
  move-focus up \; \
  move-focus up \; \
  split-pane -H --size 0.5 \
    wsl.exe -d Ubuntu --cd "$DIR/v5t11-parcours"