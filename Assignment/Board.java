/*********************************************************************
 * A class representing the game Board in the 8-puzzle problem
 *
 * Assumptions:
 * 1. Constructor receives an n-by-n array containing the n^2 integers
 *    between 0 and n^2 - 1, where 0 represents the blank square
 *********************************************************************/
import java.lang.Math;
import java.util.Random;
import java.util.Stack;
import java.lang.StringBuilder;
import java.lang.Iterable; //is it needed?
import edu.princeton.cs.algs4.In;

public class Board {
    private enum Direction {LEFT, TOP, RIGHT, BOTTOM};
    // immutable board
    private final int[][] board;
    // board dimension
    private final int dim;

    /*
     * constructs an immutable board from an n by n array of blocks
     */
    public Board(int[][] blocks) {
        this.dim = blocks.length;
        this.board = new int[this.dim][this.dim];
        for(int i = 0; i < this.dim; i++) {
            for(int j = 0; j < this.dim; j++) {
                this.board[i][j] = blocks[i][j];
            }
        }
    }

    /*
     * @return: board dimension
     */
    public int dimension() {
        return this.dim;
    }

    /* hamming distance is the number of blocks out of place
     * @return: hamming distance of this board
     */
    public int hamming() {
        int counter = 0;
        for(int r = 0; r < this.dim; r++) {
            for(int c = 0; c < this.dim; c++) {
                if(this.board[r][c] == 0) continue;
                if(this.board[r][c] != getBlockNum(r, c) ) {
                    counter++;
                }
            }
        }
        return counter;
    }

    /*
     * @return: sum of manhattan distances between blocks and goal
     */
    public int manhattan() {
        int count = 0;
        int row = -1;
        int col = -1;
        int num = -1;
        for(int r = 0; r < this.dim; r++) {
            for(int c = 0; c < this.dim; c++) {
                num = this.board[r][c];
                // 0 is not considered a block
                if(num == 0) continue;
                row = getRow(num);
                col = getCol(num, row);
                // add vertical distance
                count += Math.abs(row - r);
                // add horizontal distance
                count += Math.abs(col - c);
            }
        }
        return count;
    }

    /*
     * @return: true if board is the goal/solution board
     *          false otherwise
     */
    public boolean isGoal() {
        for(int r = 0; r < this.dim; r++) {
            for(int c = 0; c < this.dim; c++) {
                if(this.board[r][c] != getBlockNum(r, c) ) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * @return: a Board that is obtained by exchanging any pair of blocks
     */
    public Board twin() {
        if(this.dim < 1) return null;
        // get a copy of board
        int[][] twinBlocks = copyBoard();
        Random rand = new Random();
        int r1 = rand.nextInt(this.dim);
        int c1 = rand.nextInt(this.dim);
        while(this.board[r1][c1] == 0) {
            r1 = rand.nextInt(this.dim);
            c1 = rand.nextInt(this.dim);
        }
        int r2 = rand.nextInt(this.dim);
        int c2 = rand.nextInt(this.dim);
        while(this.board[r2][c2] == 0) {
            r2 = rand.nextInt(this.dim);
            c2 = rand.nextInt(this.dim);
        }
        // swap random two blocks
        swap(twinBlocks, r1, c1, r2, c2);
        // return the modified board
        return new Board(twinBlocks);
    }

    /*
     * @return: true if this board equals y
     *          false otherwise
     */
    public boolean equals(Object y) {
        if(y == null) return false;
        if(y == this) return true;
        if(this.getClass() != y.getClass()) return false;
        Board that = (Board) y;
        if(this.dim != that.dim) return false;
        for(int r = 0; r < this.dim; r++) {
            for(int c = 0; c < this.dim; c++) {
                if(this.board[r][c] != that.board[r][c]) return false;
            }
        }
        return true;
    }

    /*
     * @return: An iterable object of all neighboring boards
     */
    public Iterable<Board> neighbors() {
        Stack<Board> s = new Stack<Board>();
        boolean found = false;
        int r = 0;
        int c = 0;
        // find the position of blank square
        for(r = 0; r < this.dim; r++) {
            for(c = 0; c < this.dim; c++) {
                if(this.board[r][c] == 0) {
                    found = true;
                    break;
                }
            }
            if(found) break;
        }
        // left neighbor
        if(c - 1 >= 0) {
            s.push(constructNeighbor(Direction.LEFT, r, c));
        }
        // right neighbor
        if(c+1 < this.dim) {
            s.push(constructNeighbor(Direction.RIGHT, r, c));
        }
        // top neighbor
        if(r-1 >= 0) {
            s.push(constructNeighbor(Direction.TOP, r, c));
        }
        // bottom neighbor
        if(r + 1 < this.dim) {
            s.push(constructNeighbor(Direction.BOTTOM, r, c));
        }
        return s;
    }

    /*
     * @return: String representation of this board
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.dim + "\n");
        for(int r = 0; r < this.dim; r++) {
            for(int c = 0; c < this.dim; c++) {
                s.append(String.format("%2d ",this.board[r][c]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    /* helper function
     * @return: the correct value occupying the specified row and col
     */
    private int getBlockNum(int row, int col) {
        if(row == this.dim-1 && col == this.dim-1) return 0;
        return (this.dim * row + col + 1);
    }
    private int getRow(int num) {
        if(num % this.dim == 0) return ((num/this.dim)-1);
        return (int)Math.floor((num / this.dim));
    }
    private int getCol(int num, int row) {
        return (num - (this.dim * row)-1 );
    }
    /*
     * helper function to swap two blocks
     */
    private void swap(int[][] blocks, int r1, int c1, int r2, int c2) {
        int temp = blocks[r1][c1];
        blocks[r1][c1] = blocks[r2][c2];
        blocks[r2][c2] = temp;
    }
    /*
     * @return: a copy of this board
     */
    private int[][] copyBoard() {
        int[][] cpy = new int[this.dim][this.dim];
        for(int i = 0; i < this.dim; i++) {
            for(int j = 0; j < this.dim; j++) {
                cpy[i][j] = this.board[i][j];
            }
        }
        return cpy;
    }

    /*
     * helper function to create a neighbor board
     * @param dir: which neighbor to create (LEFT, RIGHT, TOP, BOTTOM)
     * @param row, col: position of the block for which we're creating neighbor
     * @return: Board object representing the desired neighbor
     */
    private Board constructNeighbor(Direction dir, int row, int col) {
        int[][] neighborBlocks = copyBoard();
        switch(dir) {
            case LEFT:
                swap(neighborBlocks, row, col, row, col-1);
                break;
            case TOP:
                swap(neighborBlocks, row, col, row-1, col);
                break;
            case RIGHT:
                swap(neighborBlocks, row, col, row, col+1);
                break;
            case BOTTOM:
                swap(neighborBlocks, row, col, row+1, col);
                break;
        }
        return new Board(neighborBlocks);
    }

    /*
     * test client
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] initial = new int[n][n];
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                initial[i][j] = in.readInt();
            }
        }
        Board puzzleBoard = new Board(initial);
        for(int i = 0; i < 1000; i++) {
            Board twinBoard = puzzleBoard.twin();
            if(twinBoard == null) {
                System.out.println("NO TWIN!");
                break;
            }
            System.out.printf("twin: %d\n", i);
            System.out.println(twinBoard);
        }
    }
}
