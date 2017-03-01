/*********************************************************************
 * Java implementation to solve the 8-puzzle problem using
 * the A* search algorithm
 *
 * dependencies: princeton algs4 library
 *
 * Compilation: javac-algs4 Solver.java
 * Execution: java-algs4 Solver <input.txt>
*********************************************************************/
import java.lang.NullPointerException;
import edu.princeton.cs.algs4.MinPQ;
import java.lang.NullPointerException;
import java.lang.Iterable;
import edu.princeton.cs.algs4.In;
import java.util.Comparator;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
public class Solver {

    // class that represents the node in the game tree
    private class SearchNode {
        public final Board board;
        public final int moves;     // number of moves made from the initial board to this board
        public final int manhattan;
        public final int hamming;
        public SearchNode parent;

        public SearchNode(Board board, SearchNode parent, int moves, int manhattan, int hamming) {
            this.board = board;
            this.parent = parent;
            this.moves = moves;
            this.manhattan = manhattan;
            this.hamming = hamming;
        }
    }

    /*
     * comparable object supplied to MinPQ for computing priority
     */
    private class ByManhattan implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode x, SearchNode y) {
            int xManh = x.moves + x.manhattan;
            int yManh = y.moves + y.manhattan;
            if(xManh > yManh) return 1;
            else if(xManh < yManh) return -1;
            else if(xManh == yManh) {
                if(x.moves+x.hamming > y.moves +y.hamming) return 1;
                else if(x.moves+x.hamming < y.moves +y.hamming) return -1;
                return 0;
            }
            return 0;
        }
    }
    private Board initialBoard;     // board we're searching solution for
    private SearchNode finalNode;   // node representing the solved board
    private boolean isSolvable;     // indicates if initialBoard is solvable or not

    /*
     * finds a solution to the initial board using A* algorithm
     */
    public Solver(Board initial) {
        boolean origTurn = true;
        this.finalNode = null;

        this.initialBoard = initial;
        Board twin = initial.twin();

        MinPQ<SearchNode> queue;
        MinPQ<SearchNode> origQ = new MinPQ<SearchNode>(new ByManhattan());
        MinPQ<SearchNode> twinQ = new MinPQ<SearchNode>(new ByManhattan());

        SearchNode origNode = new SearchNode(initial, null, 0, initial.manhattan(), initial.hamming());
        SearchNode twinNode = new SearchNode(twin, null, 0, twin.manhattan(), twin.hamming());

        origQ.insert(origNode);
        twinQ.insert(twinNode);
        queue = origQ;
        while(!queue.isEmpty()) {
            queue = origTurn ? origQ : twinQ;
            SearchNode dequeued = queue.delMin();
            if(dequeued.board.isGoal()) {
                if(origTurn)
                    this.finalNode = dequeued;
                break;
            }
            for(Board neighbor : dequeued.board.neighbors()) {
                if(dequeued.parent != null && neighbor.equals(dequeued.parent.board)) continue;
                SearchNode neighborNode = new SearchNode(neighbor, dequeued, dequeued.moves+1, neighbor.manhattan(), neighbor.hamming());
                queue.insert(neighborNode);
            }
            // toggle between original and twin boards
            origTurn = !origTurn;
        }
        if(origTurn && this.finalNode!=null) isSolvable = true;
        else isSolvable = false;
    }

    /*
     * @return: true if board is solvable
     *          false otherwise
     */
    public boolean isSolvable() {
        return this.isSolvable;
    }

    /*
     * @return: min number of moves to solve initial board
     *          -1 if unsolvable
     */
    public int moves() {
        if(this.finalNode == null) return -1;
        return this.finalNode.moves;
    }

    /*
     * @return: sequence of boards in the shortest solution
     *          null if unsolvable
     */
    public Iterable<Board> solution() {
        // unsolvable board
        if(this.finalNode==null) return null;
        LinkedList<Board> path = new LinkedList<Board>();
        SearchNode k = finalNode;
        while(k != null) {
            path.addFirst(k.board);
            k = k.parent;
        }
        return path;

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
        Solver algorithm = new Solver(puzzleBoard);
        if(!algorithm.isSolvable()) {
            System.out.println("board not solvable");
            return;
        }
        System.out.printf("problem solved in %d steps\n", algorithm.moves());
        System.out.println("------------PATH------------");
        for(Board b : algorithm.solution()) {
            System.out.println(b);
        }
        if(algorithm.isSolvable()) System.out.println("board is solvable");
        else System.out.println("board is unsolvable");
    }
}
