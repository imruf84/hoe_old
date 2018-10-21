#!/bin/sh
pkill Xvfb
Xvfb :1 -screen 0 1024x768x24 </dev/null &
export DISPLAY=":1"