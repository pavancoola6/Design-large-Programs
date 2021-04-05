# Scrabble Game

Introduction and summary of project.

The program is the JavaFX GUI application that allows player (human) plays the Scrabble with the computer.
The player and computer has 7 letters with scores (tiles).
The player and computer take turn to put tiles in the board.
Each square in the board has number that is 2 (double score), 3 (triple score).
The system will calculate the scores after each turn.
The system will delivers the tiles in order to have 7 tiles for each player (if there is still remaining tiles)
Game over when the player or computer plays all the tiles


### Usage

Instructions for using program.

1. Console - WordSolver

java -jar Solver.jar sowpods.txt < input.txt
or
java -jar Solver.jar sowpods.txt < example_input.txt

2. GUI

Open the project in Intellij Idea IDE and run Scrabble class

3. Run jar

java -jar ScrabbleGame.jar

Note: The text files must be in the same folder with jar

scrabble_board.txt
scrabble_tiles.txt
sowpods.txt

### Project Assumptions

List any assumptions outside of one's specified in project writeup.

Assume that the player put the valid tiles on board.
The scores are in the GUI. So, the player will compare scores and know who wins.

### References

  Word Solver
 * http://www.cs.cmu.edu/afs/cs/academic/class/15451-s06/www/lectures/scrabble.pdf
 * https://ericsink.com/downloads/faster-scrabble-gordon.pdf
 * https://www.scotthyoung.com/blog/2013/02/21/wordsmith/
 * https://zylsciencefan.wordpress.com/2012/12/21/a-decision-making-algorithm-for-scrabble/
 * http://rmandvikar.blogspot.com/2008/08/scrabble-algorithm.html
 

