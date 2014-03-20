MineSweeper
========

This is my initial implementation of MineSweeper on Google Glass

Currently, you're presented a 9x9 mine field. Move the yellow dot about the field 
by moving your head. Speak "reveal" to reveal a tile.

## Running on Glass

You can use your IDE to compile and install the sample or use
[`adb`](https://developer.android.com/tools/help/adb.html)
on the command line:

    $ adb install -r MineSweeper.apk

To start the sample, say "ok glass, minesweepwe" from the Glass clock
screen or use the touch menu.

## TODO
I plan to implement the following features (in no particular order):
* Use Graphics instead of text
* Scoreboard
* Timer
* Selectable difficultly
* Use Gestures
* Others as I think of them
