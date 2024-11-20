package MazeGen;


import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

   char[][] maze;
   Stack<int[]> stack;
   ArrayList<int[]> visited;
   int x;
   int y;

   int rows;
   int cols;
   int endX;
   int endY;
   int[] directions;

   public static final Random rnd = new Random();

   public Generator(int rows, int cols) {

      this.stack = new Stack<>();
      this.visited = new ArrayList<>();
      this.rows = rows;
      this.cols = cols;
      createMaze(rows, cols);
      //0: Left, 1: Right, 2: Up, 3: Down
      this.directions = new int[]{0, 1, 2, 3};
   }

   public void setStart(int x, int y) {

      this.x = x;
      this.y = y;
      maze[x][y] = 'S';
      stack.push(new int[]{x, y});
      visited.add(new int[]{x, y});
   }

   public void setEnd(int x, int y) {

      this.endX = x; // Store end X coordinate
      this.endY = y; // Store end Y coordinate
      maze[x][y] = 'E';
   }

   public int[] getEnd() {

      return new int[]{endX, endY};
   }

   public int getX() {

      return this.x;
   }

   public int getY() {

      return this.y;
   }

   public void createMaze(int height, int width) {

      maze = new char[height][width];

      // Fill the maze with walls
      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            maze[i][j] = '■';  // Fill with walls
         }
      }
   }

   public int[] getNewDir() {

      int newX = x;
      int newY = y;

      int direction = rnd.nextInt(4);

      switch (direction) {
         case 0:
            newY--;
            break; // LEFT
         case 1:
            newY++;
            break; // RIGHT
         case 2:
            newX--;
            break; // UP
         case 3:
            newX++;
            break; // DOWN
         default:
            throw new IllegalStateException("Unexpected value: " + direction);
      }
      return new int[]{newX, newY};
   }


   public void move() {

      ArrayList<int[]> dirTaken = new ArrayList<>();

      for (int attempts = 0; attempts < 4; attempts++) {
         // Get a new direction
         int[] newDir = getNewDir();
         dirTaken.add(newDir);
         if (dirTaken.contains(newDir)) {
            newDir = getNewDir();
         }
         int newX = newDir[0];
         int newY = newDir[1];

         // Check if new position is within bounds and is a wall or end
         if (newX > 0 && newX < rows - 1 && newY > 0 && newY < cols - 1 &&
                 (maze[newX][newY] == '■' || maze[newX][newY] == 'E') &&
                 !moreThanOneVisited(newX, newY)) {

            // Move to the new position
            x = newX;
            y = newY;
            stack.push(new int[]{x, y});
            maze[x][y] = ' '; // Mark as path
            visited.add(new int[]{x, y});
            return; // Exit after a successful move
         }
      }

      // If no valid moves were found after 4 attempts, backtrack
      visited.add(new int[]{x, y});
      backtrack();
   }

   public void backtrack() {

      if (!stack.isEmpty()) {
         int[] lastPosition = stack.pop();
         x = lastPosition[0];
         y = lastPosition[1];

      } else {
         move();
      }
   }

   public boolean moreThanOneVisited(int newX, int newY) {

      int visitedCount = 0;

      // Check all four adjacent cells
      if (isVisited(newX - 1, newY)) visitedCount++; // Left
      if (isVisited(newX + 1, newY)) visitedCount++; // Right
      if (isVisited(newX, newY - 1)) visitedCount++; // Up
      if (isVisited(newX, newY + 1)) visitedCount++; // Down

      return visitedCount > 1; // Return true if more than one visited cell
   }

   // Helper method to check if a position is visited
   private boolean isVisited(int x, int y) {

      for (int[] position : visited) {
         if (position[0] == x && position[1] == y) {
            return true;
         }
      }
      return false;
   }

   public void printMaze() {

      StringBuilder mazeBuilder = new StringBuilder();

      for (char[] chars : maze) {
         for (char aChar : chars) {
            mazeBuilder.append("  ").append(aChar); // Append each character
         }
         mazeBuilder.append('\n');
      }

      // Write to file
      try (FileWriter writer = new FileWriter("maze.txt")) {
         writer.write(mazeBuilder.toString()); // Write the maze directly
         System.out.println("Success");
      } catch (IOException e) {
         System.out.println("An error occurred while writing to the file.");
      }
   }

   public static void main(String[] args) {

      long startTime = System.nanoTime();

      int size = 51;

      Generator generator = new Generator(size, size);
      generator.setStart(size - 2, 1);
      generator.setEnd(1, size - 2);

      while (!Arrays.equals(new int[]{generator.getX(), generator.getY()}, generator.getEnd())) {
         generator.move();
      }
      generator.printMaze();

      long endTime = System.nanoTime();
      long duration = (endTime - startTime) / 1_000_000_000;
      System.out.println("Execution Time: " + duration);

   }
}