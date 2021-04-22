# Project 4: Mazes
# Instructors : Brooke Chenoweth and Joseph Haugh

Welcome to Pavan Kumar Singara and Anmol Gill Singh Mazes project. This project is created as the 4th project assignment of the CS-351L.

# Maze Generation Algorithms !

1. Randomized Depth First Search 
2. Randomized Kruskal’s
3. Randomized Prim’s
4. Aldous-Broder
5. Recursive Division

#Input

The required command line argument for running this program is simply {filename} (Where {filename} = name of configuration file).
Make sure to always run it with this program argument. But in our jar file we already added "testfile.txt" as program argument basically it is not required to specifically add inorder to run.

Within this "testfile" the first line should be formatted as follows:
{screen-size} {cell-size} {generator} {solver}

Example:
700 50 kruskal mouse

Constraints:
100 <= {screen-size} <= 900
10 <= {cell-size} <= 20
{generator} = dfs, kruskal, prim                           (Pick one of these)
{solver} = mouse, wall, pledge, wall_thread, mouse_thread  (Pick one of these)


#Description

On program run, the chosen generator generates the maze.  Then, a start and end point are picked, denoted by a green and red cell, respectively.
The chosen solver precedes to solve the maze, where the solver's location is represented by a purple cell.  When the solver finds the exit to the maze, the program ends.
Both generation and solving are animated, and can each be sped up or slowed down by modifying the "delay" parameter passed into the generator.generateMaze() function, 
and the "delay" parameter passed into the solver.solveMaze() function.  Both of these functions are called only once in the Driver class.


#Algorithm References

Generators:
Depth-first Search  -  https://en.wikipedia.org/wiki/Maze_generation_algorithm#Randomized_depth-first_search
Kruskal  -  https://en.wikipedia.org/wiki/Maze_generation_algorithm#Randomized_Kruskal's_algorithm
Prim  -  https://en.wikipedia.org/wiki/Maze_generation_algorithm#Randomized_Prim's_algorithm

Solvers:
Random Mouse  -  https://en.wikipedia.org/wiki/Maze-solving_algorithm#Random_mouse_algorithm
Wall Follower  -  https://en.wikipedia.org/wiki/Maze-solving_algorithm#Wall_follower
Pledge  -  https://en.wikipedia.org/wiki/Maze-solving_algorithm#Pledge_algorithm
