import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
public class Solver {    
    
    private  boolean isSolvable = false;
    private int moves = -1;
    private List<Board> solution = null;

    private class Node implements Comparable<Node> {
        private final Board board;
        private final Node prev;
        private final int moves;
        private final int priority;

        public Node(Board board, Node prev, int moves) {
            this.board = board;
            this.prev = prev;
            this.moves = moves;
            this.priority = board.manhattan() + moves;
        }

        public int compareTo(Node other) {
            return this.priority - other.priority;
        }
    }

    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }

        MinPQ<Node> pq = new MinPQ<Node>();
        MinPQ<Node> twinPq = new MinPQ<Node>();
        solution = new ArrayList<Board>();
        isSolvable = false;
        pq.insert(new Node(initial, null, 0));
        twinPq.insert(new Node(initial.twin(), null, 0));

        while (!pq.isEmpty() && !twinPq.isEmpty()) {
            Node minNode = pq.delMin();
            Node twinMinNode = twinPq.delMin();
            if (minNode.board.isGoal()) {
                isSolvable = true;
                moves = minNode.moves;
                while (minNode != null) {
                    solution.add(minNode.board);
                    minNode = minNode.prev;
                }
                Collections.reverse(solution);
                return;
            }

            if (twinMinNode.board.isGoal()) {
                isSolvable = false;
                moves = -1;
                solution = null;
                return;
            }

            for (Board neighbor : minNode.board.neighbors()) {
                if (minNode.prev != null && minNode.prev.board.equals(neighbor)) {
                    continue;
                }
                pq.insert(new Node(neighbor, minNode, minNode.moves + 1));
            }

            for (Board twinNeighbor : twinMinNode.board.neighbors()) {
                if (twinMinNode.prev != null && twinMinNode.prev.board.equals(twinNeighbor)) {
                        continue;
                }
                twinPq.insert(new Node(twinNeighbor, twinMinNode, twinMinNode.moves + 1));
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {

        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        
        return moves;
    }

    public Iterable<Board> solution() {
        if (!isSolvable) {
            return null;
        }
        return Collections.unmodifiableList(solution);

    }


    public static void main(String[] args)  {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }    
        Board initial = new Board(tiles);

        Solver solver = new Solver(initial);

        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        }
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }

}
