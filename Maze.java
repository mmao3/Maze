import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Maze {
	// dimension of maze
	private int n;
	
	//whether there is wall to the north/east/south/west of cell(i,j) 
	private boolean [][][] mazeMatrix;
	
	//whether cell (i,j)  is visited 
	private boolean[][] visited;
	
	// whether reached the destination
	private boolean reached;
	
	// random generate a number 
    private Random random;
    
    
	
	 /**
     * Constructs a new maze with the dimension of n
     *
     * @param  n the dimension of maze
     * @throws IllegalArgumentException unless {@code n > 0}
     */
	public Maze(int n) {
		if(n <= 0) {
			  throw new IllegalArgumentException("Dimension of maze must be positive");
		}
		this.n = n;
		initialize();
		randomGenerator(1,1);		
	}
	
	 /**
     * Constructs a new maze according to the specified wall matrix
     *
     * @param  mazeMatrix the wall matrix to the north/east/south/west of cell(i,j) 
     * @throws NullPointerException if {@code mazeMatrix == null} or {@code mazeMatrix[0] == null} or {@code mazeMatrix[0][0] == null}
     * @throws IllegalArgumentException unless {@code mazeMatrix} and  {@code mazeMatrix[0]} have same length and {@code mazeMatrix[0][0].length == 4}
     */
	public Maze(boolean [][][] mazeMatrix) {
		 if (mazeMatrix == null || mazeMatrix[0] == null || mazeMatrix[0][0] == null) {
			 throw new NullPointerException();
		 }
		 if(mazeMatrix.length == 0 || mazeMatrix[0].length == 0 || mazeMatrix[0][0].length == 0 || mazeMatrix.length != mazeMatrix[0].length || mazeMatrix[0][0].length != 4) {
			 throw new IllegalArgumentException("Maze must be square and there must be 4 walls in each cell");
		 }
		 n = mazeMatrix.length;
		 initialize();
		 
		 // copy mazeMatrix
		 for(int i = 0; i < n; i++)  {
			 for(int j = 0; j < n; j++) {
				 for(int k = 0; k < 4; k++) {
					 this.mazeMatrix[i+1][j+1][k] = mazeMatrix[i][j][k];
				 }
			 }
		 }
		 
	}
	
	private void initialize() {
		// make the user coordinate to 0 - n+2 
		QuickDraw.setXscale(0, n+2);
		QuickDraw.setYscale(0, n+2);
		visited = new boolean[n+2][n+2];
		reached = false;
		random =  new Random();
		
		// make border cells as already visited
        for (int i = 0; i < n+2; i++) {
            visited[i][0] = true;
            visited[i][n+1] = true;
            visited[0][i] = true;
            visited[n+1][i] = true;
        }
        
        mazeMatrix =  new boolean[n+2][n+2][4];
        // make all walls as present
        for (int x = 0; x < n+2; x++) {
            for (int y = 0; y < n+2; y++) {
            	mazeMatrix[x][y][0] = true;
            	mazeMatrix[x][y][1]  = true;
            	mazeMatrix[x][y][2] = true;
            	mazeMatrix[x][y][3]  = true;
            }
        }
	}
	
	private void randomGenerator(int x, int y) {
        visited[x][y] = true;

        // while there is an unvisited neighbor
        while (!visited[x][y+1] || !visited[x+1][y] || !visited[x][y-1] || !visited[x-1][y]) {

            // pick random neighbor 
            while (true) {
                int r = random.nextInt(4);
                if (r == 0 && !visited[x][y+1]) {
                	mazeMatrix[x][y][0] = false;
                	mazeMatrix[x][y+1][2] = false;
                	randomGenerator(x, y + 1);
                    break;
                }
                else if (r == 1 && !visited[x+1][y]) {
                	mazeMatrix[x][y][1] = false;
                	mazeMatrix[x+1][y][3] = false;
                    randomGenerator(x+1, y);
                    break;
                }
                else if (r == 2 && !visited[x][y-1]) {
                	mazeMatrix[x][y][2] = false;
                	mazeMatrix[x][y-1][0] = false;
                	randomGenerator(x, y-1);
                    break;
                }
                else if (r == 3 && !visited[x-1][y]) {
                	mazeMatrix[x][y][3] = false;
                	mazeMatrix[x-1][y][1] = false;
                    randomGenerator(x-1, y);
                    break;
                }
            }
        }
    }
	
	private void solve(int startX, int startY, int endX, int endY, List<int[]> path) {
        if (startX == 0 || startY == 0 || startX == n+1 || startY == n+1 || visited[startX][startY] || reached) {
        	
        	return;
        }
   
        visited[startX][startY] = true;
        path.add(new int[]{startX,startY});
        QuickDraw.setPenColor(QuickDraw.BLUE);
        QuickDraw.filledCircle(startX + 0.5, startY + 0.5, 0.25);
        QuickDraw.show();
        QuickDraw.pause(30);
      
        // reached the destination
        if (startX == endX && startY == endY) {
        	reached = true;
        }

        if (!mazeMatrix[startX][startY][0]) {
        	solve(startX, startY + 1, endX, endY, path);
        }
        if (!mazeMatrix[startX][startY][1]) {
        	solve(startX + 1, startY, endX, endY, path);
        }
        if (!mazeMatrix[startX][startY][2]) {
        	solve(startX, startY - 1, endX, endY, path);
        }
        if (!mazeMatrix[startX][startY][3]){
        	solve(startX - 1, startY, endX, endY, path);
        }

        if (reached) {
        	return;
        }
        path.remove(path.size() - 1);
        QuickDraw.setPenColor(QuickDraw.GRAY);
        QuickDraw.filledCircle(startX + 0.5, startY + 0.5, 0.25);
        QuickDraw.show();
        QuickDraw.pause(30);
    }
	
	 /**
     * returns the path from start point to end point
     *
     * @param  startX x coordinate of start point
     * @param  startY y coordinate of start point
     * @param  endX x coordinate of end point
     * @param  endY y coordinate of end point
     * @return the path from start point to end point stored in a list
     * @throws IllegalArgumentException unless {@code startX >= 1} and {@code startX <= n}
     *         and {@code startY >= 1} and {@code startY <= n} and {@code endX >= 1} 
     *         and {@code endX <= n} and {@code endY >= 1} and {@code endY <= n}
     */
	public List<int[]> solve(int startX, int startY, int endX, int endY) {
		if(!( startX >=1 && startX <= n && startY >=1 && startY <= n && endX >= 1 && endX <= n && endY >= 1 && endY <= n)) {
			throw new IllegalArgumentException("");
		}
		
	    // use red circle to represent start point and green circle to represent end point 
		QuickDraw.setPenColor(QuickDraw.RED);
		QuickDraw.filledCircle(startX  + 0.5, startY + 0.5, 0.375);
		QuickDraw.setPenColor(QuickDraw.GREEN);
		QuickDraw.filledCircle(endX + 0.5, endY + 0.5, 0.375);
		QuickDraw.show();
		
		for (int x = 1; x <= n; x++) {
			for (int y = 1; y <= n; y++) {
				visited[x][y] = false;
			}		
		}
		List<int[]> path = new ArrayList<int[]>();
		solve(startX, startY, endX, endY, path);
		return path;
	}
	
	 /**
     * displays the maze
     */
	public void display() {
        for (int x = 1; x <= n; x++) {
            for (int y = 1; y <= n; y++) {
                if (mazeMatrix[x][y][2]) {
                	QuickDraw.line(x, y, x+1, y);
                }
                if (mazeMatrix[x][y][0]) {
                	QuickDraw.line(x, y+1, x+1, y+1);
                }
                if (mazeMatrix[x][y][3]) {
                	QuickDraw.line(x, y, x, y+1);
                } 
                if (mazeMatrix[x][y][1])  {
                	QuickDraw.line(x+1, y, x+1, y+1);
                }
            }
        }
        QuickDraw.show();
    }
	
	public static void main(String[] args) { 
		int n = args != null && args.length > 0 ? Integer.parseInt(args[0]) : 10;
        Maze maze = new Maze(n);
        
        // enable animation
        QuickDraw.enableDefer();
        maze.display();
        List<int[]> path = new ArrayList<int[]>();
        
        int startX = args != null && args.length > 4 ? Integer.parseInt(args[1]) : 1; 
        int startY = args != null && args.length > 4 ? Integer.parseInt(args[2]) : 1;
        int endX = args != null && args.length > 4 ? Integer.parseInt(args[3]) : 5; 
        int endY = args != null && args.length > 4 ? Integer.parseInt(args[4]) : 5;
        path = maze.solve(startX, startY, endX, endY);
        
        // print the path in the format of "(x0, y0) (x1, y1) (x2, y2) .... "
        for(int[] p : path) {
        	System.out.print("( " + p[0] + ", " + p[1] + " ) ");
        }
    }
}
