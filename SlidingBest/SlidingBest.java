import java.util.LinkedList;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//This program solves Num/R/G puzzle using best first search and misplace heuristic 
public class SlidingBest {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String inputFileName;
        String outputFileName;

        try {
            // take user inputs for files
            System.out.println("Enter input file name then output file name:");
            inputFileName = input.next();
            outputFileName = input.next();

            // use user inputs for both files
            File inputFile = new File(inputFileName);
            File outputFile = new File(outputFileName);

            // scanner to read file
            Scanner read = new Scanner(inputFile);

            // get size of board
            int n = Integer.parseInt(read.next());

            // create string matrix using size
            String[][] initial = new String[n][n];
            String[][] goal = new String[n][n];

            // read values into boards
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    initial[i][j] = read.next();
                }
            }
            // initialize counters for goal board
            int nums = 0;
            int r = 0;
            // initialize array for numbers
            int[] numbers = new int[n * n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // count number of R's
                    if (initial[i][j].equals("R")) {
                        r++;
                        // count number of numbers
                    } else if (!initial[i][j].equals("G")) {
                        // convert and add numbers to integer array
                        numbers[nums] = Integer.parseInt(initial[i][j]);
                        nums++;
                    }
                }
            }
            // basic selection sort to order numbers
            for (int i = 0; i < nums - 1; i++) {
                int minIndex = i;
                for (int j = i + 1; j < nums; j++) {
                    if (numbers[j] < numbers[minIndex]) {
                        minIndex = j;
                    }
                }
                int temp = numbers[i];
                numbers[i] = numbers[minIndex];
                numbers[minIndex] = temp;
            }
            // counter to increment through number array
            int ctr = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // for each number
                    if (ctr < nums) {
                        // store converted string value into goal matrix and increment counter
                        goal[i][j] = String.valueOf(numbers[ctr]);
                        ctr++;
                    } // for each R store value into goal matrix
                    else if (r > 0) {
                        goal[i][j] = "R";
                        r--;

                    } // for each G store value into goal matrix
                    else {
                        goal[i][j] = "G";
                    }
                }
            }

            // setup writer
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // create object and solve
            SlidingBest s = new SlidingBest(initial, goal, n, writer);
            s.solve();

            // close scanners and writer
            writer.close();
            read.close();
            input.close();
        } catch (

        IOException e) {
            System.out.println(e.getMessage());
        }

    }

    // Board class (inner class)
    private class Board {
        private String[][] array; // board array
        private int hvalue; // heuristic value
        private Board parent; // parent board

        // Constructor of board class
        private Board(String[][] array, int size) {
            this.array = new String[size][size]; // create board array

            for (int i = 0; i < size; i++) // copy given array
                for (int j = 0; j < size; j++)
                    this.array[i][j] = array[i][j];

            this.hvalue = 0; // heuristic value is 0
            this.parent = null; // no parent
        }
    }

    private Board initial; // initial board
    private Board goal; // goal board
    private int size; // board size
    private BufferedWriter writer; // file writer

    // Constructor of SlidingBest class
    public SlidingBest(String[][] initial, String[][] goal, int size, BufferedWriter writer) {
        this.size = size; // set size of board
        this.initial = new Board(initial, size); // create initial board
        this.writer = writer; // set writer
        this.goal = new Board(goal, size); // create gosl board
    }

    // Method solves sliding puzzle
    public void solve() {
        LinkedList<Board> openList = new LinkedList<Board>(); // open list
        LinkedList<Board> closedList = new LinkedList<Board>();// closed list

        openList.addFirst(initial); // add initial board to open list

        while (!openList.isEmpty()) // while open list has more boards
        {
            int best = selectBest(openList); // select best board

            Board board = openList.remove(best); // remove board

            closedList.addLast(board); // add board to closed list

            if (goal(board)) // if board is goal
            {
                displayPath(board); // display path to goal
                return; // stop search
            } else // if board is not goal
            {
                LinkedList<Board> children = generate(board);// create children

                for (int i = 0; i < children.size(); i++) {
                    Board child = children.get(i); // for each child

                    if (!exists(child, openList) && !exists(child, closedList))
                        openList.addLast(child); // if child is not in open and
                } // closed lists then add it to
            } // open list
        }

        System.out.println("no solution"); // no solution if there are
    } // no boards in open list

    // Method creates children of a board
    private LinkedList<Board> generate(Board board) {
        LinkedList<Board> children = new LinkedList<Board>();// list of children

        // for each position find where it can swap
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // get current value the loop is on
                String current = board.array[i][j];
                // if not in top row
                if (i > 0) {
                    // if item can swap upwards
                    if (!current.equals(board.array[i - 1][j])) {
                        // create child and add to list
                        children.addLast(createChild(board, i, j, 'N'));
                    }
                }
                // if not on bottom row
                if (i < size - 1) {
                    // if item can swap downwards
                    if (!current.equals(board.array[i + 1][j])) {
                        // create child and add to list
                        children.addLast(createChild(board, i, j, 'S'));
                    }
                }
                // if not on right side
                if (j < size - 1) {
                    // if item can swap right
                    if (!current.equals(board.array[i][j + 1])) {
                        // create child and add to list
                        children.addLast(createChild(board, i, j, 'E'));
                    }
                }
                // if not on left side
                if (j > 0) {
                    // if item can swap left
                    if (!current.equals(board.array[i][j - 1])) {
                        // create child and add to list
                        children.addLast(createChild(board, i, j, 'W'));
                    }
                }
            }
        }

        return children; // return children
    }

    // Method creates a child of a board by swapping in certain direction
    private Board createChild(Board board, int i, int j, char direction) {
        Board child = copy(board); // create copy of board

        if (direction == 'N') // swap with north
        {
            String temp = child.array[i][j];
            child.array[i][j] = child.array[i - 1][j];
            child.array[i - 1][j] = temp;
        } else if (direction == 'S') // swap with south
        {
            String temp = child.array[i][j];
            child.array[i][j] = child.array[i + 1][j];
            child.array[i + 1][j] = temp;
        } else if (direction == 'E') // swap with east
        {
            String temp = child.array[i][j];
            child.array[i][j] = child.array[i][j + 1];
            child.array[i][j + 1] = temp;
        } else // swap with west
        {
            String temp = child.array[i][j];
            child.array[i][j] = child.array[i][j - 1];
            child.array[i][j - 1] = temp;
        }

        child.hvalue = heuristic_M(child); // set heuristic value

        child.parent = board; // assign parent to child

        return child; // return child
    }

    // Method computes heuristic value of board
    // Heuristic value is the number misplaced values
    private int heuristic_M(Board board) {
        int value = 0; // initial heuristic value

        for (int i = 0; i < size; i++) // go thru board and
            for (int j = 0; j < size; j++) // count misplaced values
                if (!board.array[i][j].equals(goal.array[i][j])) //use .equals for string
                    value += 1;

        return value; // return heuristic value
    }

    // Method locates the board with minimum heuristic value in a
    // list of boards
    private int selectBest(LinkedList<Board> list) {
        int minValue = list.get(0).hvalue; // initialize minimum
        int minIndex = 0; // value and location

        for (int i = 0; i < list.size(); i++) {
            int value = list.get(i).hvalue;
            if (value < minValue) // updates minimums if
            { // board with smaller
                minValue = value; // heuristic value is found
                minIndex = i;
            }
        }

        return minIndex; // return minimum location
    }

    // Method creates copy of a board
    private Board copy(Board board) {
        return new Board(board.array, size);
    }

    // Method decides whether a board is goal
    private boolean goal(Board board) {
        return identical(board, goal); // compare board with goal
    }

    // Method decides whether a board exists in a list
    private boolean exists(Board board, LinkedList<Board> list) {
        for (int i = 0; i < list.size(); i++) // compare board with each
            if (identical(board, list.get(i))) // element of list
                return true;

        return false;
    }

    // Method decides whether two boards are identical
    private boolean identical(Board p, Board q) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (!p.array[i][j].equals(q.array[i][j]))
                    return false; // if there is a mismatch then false

        return true; // otherwise true
    }

    // Method displays path from initial to current board
    private void displayPath(Board board) {
        LinkedList<Board> list = new LinkedList<Board>();

        Board pointer = board; // start at current board

        while (pointer != null) // go back towards initial board
        {
            list.addFirst(pointer); // add boards to beginning of list

            pointer = pointer.parent; // keep going back
        }
        // print boards in list
        for (int i = 0; i < list.size(); i++)
            displayBoard(list.get(i));
    }

    // Method displays board
    private void displayBoard(Board board) {
        for (int i = 0; i < size; i++) // print each element of board and write to file
        {
            for (int j = 0; j < size; j++) {
                System.out.print(board.array[i][j] + " ");
                try {
                    writer.write(board.array[i][j] + " ");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            try {
                // new line between lines
                writer.newLine();
                System.out.println();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            // new line between boards
            System.out.println();
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}