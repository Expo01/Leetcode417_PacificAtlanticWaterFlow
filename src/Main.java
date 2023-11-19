import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

// BFS "most intuitive" editorial
class Solution {
    private static final int[][] DIRECTIONS = new int[][]{{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
    private int numRows;
    private int numCols;
    private int[][] landHeights;

    public List<List<Integer>> pacificAtlantic(int[][] matrix) {
        // Check if input is empty
        if (matrix.length == 0 || matrix[0].length == 0) {
            return new ArrayList<>();
        }

        // Save initial values to parameters
        numRows = matrix.length;
        numCols = matrix[0].length;
        landHeights = matrix;

        // Setup each queue with cells adjacent to their respective ocean
        Queue<int[]> pacificQueue = new LinkedList<>();
        Queue<int[]> atlanticQueue = new LinkedList<>();
        for (int i = 0; i < numRows; i++) {
            pacificQueue.offer(new int[]{i, 0});
            atlanticQueue.offer(new int[]{i, numCols - 1});
        }
        for (int i = 0; i < numCols; i++) {
            pacificQueue.offer(new int[]{0, i});
            atlanticQueue.offer(new int[]{numRows - 1, i});
        }

        // Perform a BFS for each ocean to find all cells accessible by each ocean
        boolean[][] pacificReachable = bfs(pacificQueue);
        boolean[][] atlanticReachable = bfs(atlanticQueue);

        // Find all cells that can reach both oceans
        List<List<Integer>> commonCells = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (pacificReachable[i][j] && atlanticReachable[i][j]) {
                    commonCells.add(List.of(i, j)); //*******nice way to directly add items as a list instead of creating new list and adding items individually
                    //*****************************
                }
            }
        }
        return commonCells;
    }

    private boolean[][] bfs(Queue<int[]> queue) {
        boolean[][] reachable = new boolean[numRows][numCols];
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            // This cell is reachable, so mark it
            reachable[cell[0]][cell[1]] = true;
            for (int[] dir : DIRECTIONS) { // Check all 4 directions
                int newRow = cell[0] + dir[0];
                int newCol = cell[1] + dir[1];
                // Check if new cell is within bounds
                if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
                    continue;
                }
                // Check that the new cell hasn't already been visited
                if (reachable[newRow][newCol]) {
                    continue;
                }
                // Check that the new cell has a higher or equal height,
                // So that water can flow from the new cell to the old cell
                if (landHeights[newRow][newCol] < landHeights[cell[0]][cell[1]]) {
                    continue;
                }
                // If we've gotten this far, that means the new cell is reachable
                queue.offer(new int[]{newRow, newCol});
            }
        }
        return reachable;
    }
}

// editorial DFS is actually much faster
class Solution {
    private static final int[][] DIRECTIONS = new int[][]{{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
    private int numRows;
    private int numCols;
    private int[][] landHeights;

    public List<List<Integer>> pacificAtlantic(int[][] matrix) {
        // Check if input is empty
        if (matrix.length == 0 || matrix[0].length == 0) {
            return new ArrayList<>();
        }

        // Save initial values to parameters
        numRows = matrix.length;
        numCols = matrix[0].length;
        landHeights = matrix;
        boolean[][] pacificReachable = new boolean[numRows][numCols];
        boolean[][] atlanticReachable = new boolean[numRows][numCols];

        // Loop through each cell adjacent to the oceans and start a DFS
        for (int i = 0; i < numRows; i++) {
            dfs(i, 0, pacificReachable);
            dfs(i, numCols - 1, atlanticReachable);
        }
        for (int i = 0; i < numCols; i++) {
            dfs(0, i, pacificReachable);
            dfs(numRows - 1, i, atlanticReachable);
        }

        // Find all cells that can reach both oceans
        List<List<Integer>> commonCells = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (pacificReachable[i][j] && atlanticReachable[i][j]) {
                    commonCells.add(List.of(i, j));
                }
            }
        }
        return commonCells;
    }

    private void dfs(int row, int col, boolean[][] reachable) {
        // This cell is reachable, so mark it
        reachable[row][col] = true;
        for (int[] dir : DIRECTIONS) { // Check all 4 directions
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            // Check if new cell is within bounds
            if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
                continue;
            }
            // Check that the new cell hasn't already been visited
            if (reachable[newRow][newCol]) {
                continue;
            }
            // Check that the new cell has a higher or equal height,
            // So that water can flow from the new cell to the old cell
            if (landHeights[newRow][newCol] < landHeights[row][col]) {
                continue;
            }
            // If we've gotten this far, that means the new cell is reachable
            dfs(newRow, newCol, reachable); // no queue, just recursion
        }
    }
}

// time limit exceeded, passes 20
class Solution {
    int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    boolean pacificHit = false;
    boolean atlanticHit = false;

    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        List<List<Integer>> ans = new ArrayList<>();

        if (heights.length == 0 || heights[0].length == 0) {
            return ans;
        }

        for (int row = 0; row < heights.length; row++) {
            for (int col = 0; col < heights[0].length; col++) {
                int cellHeight = heights[row][col];
                DFS(row, col, heights, cellHeight);
                if (pacificHit && atlanticHit) {
                    ArrayList<Integer> curPoints = new ArrayList<>();
                    curPoints.add(row);
                    curPoints.add(col);
                    ans.add(curPoints);
                }
                pacificHit = false;
                atlanticHit = false;
            }
        }

        return ans;

    }

    private void DFS(int row, int col, int[][] heights, int precursor) {
        if (row < 0 || col < 0) {
            pacificHit = true;
            return;
        }
        if (col >= heights[0].length || row >= heights.length) {
            atlanticHit = true;
            return;
        }

        int curHeight = heights[row][col];
        if (curHeight > precursor || curHeight == -1) {
            return;
        }

        heights[row][col] = -1;

        for (int[] dir : directions) {
            DFS(row + dir[0], col + dir[1], heights, curHeight);
        } // need to mark as visited and then remove so prevent neighbors looking at each other

        heights[row][col] = curHeight;
    }
}


// first attempt
class Solution {
    int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        List<List<Integer>> ans = new ArrayList<>();

        if (heights.length == 0 || heights[0].length == 0) {
            return ans;
        }

        for (int row = 0; row < heights.length; row++) {
            for (int col = 0; col < heights[0].length; col++) {
                int cellHeight = heights[row][col];
                if (DFS(row, col, heights, cellHeight)) {
                    ArrayList<Integer> curPoints = new ArrayList<>();
                    curPoints.add(row);
                    curPoints.add(col);
                    ans.add(curPoints);
                }
            }
        }

        return ans;

    }

    private boolean DFS(int row, int col, int[][] heights, int precursor) {
        if (row < 0 || col < 0) {
            pacificHit = true;
        }
        if (col >= heights[0].length || row >= heights.length) {
            atlanticHit = true;
        }
        int curHeight = heights[row][col];
        if (curHeight > precursor) {
            return false;
        }

        for (int[] dir : directions) {
            DFS(row + dir[0], col + dir[1], heights, curHeight);
        }



        return pacificHit && atlanticHit;
    }
}


// DFS backtracking
// will need an int[] of pacific coordinates and atlantic coordinates such as [0,-1] will be first pacific cell
// to L of row and [heights.length-1,heights[0].length] would be atlantic coordinate to right of cell in last row
// don't want to list all coordinates though since graph could be 100 length...

// if r < 0 || c < 0  --> pacific hit
// if c >= heights[0].length || r >= heights.length  --> atlantic hit
// focus only on edges not the diagonal corner cells since water cannot flow to those and will not appear in search

// will need two booleans pacificHit && atlanticHit. and return pacificHit && atlanticHit. if DFS returns true
// then add cell coordinate to return list, encapsulaten in calling method, not class variable

// for DFS, will have coordinates to modify the point U,R,D,L
// if base condition met for OOB, mark atlantic or pacific as true
// if cell is > then returrn false
// if cell is < then procede to pass next cell for DFS
// use for loop to call all coordinate variations insteead of rewriting 4 times