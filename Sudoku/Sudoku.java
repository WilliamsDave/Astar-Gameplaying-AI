import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

//This program solves enhanced sudoku puzzle using constraint satisfication, backtracking, and recursion
public class Sudoku {
    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in);
            // get user input for input and output file
            System.out.println("Enter input file name:");
            String inputFileName = input.next();
            System.out.println("Enter output file name:");
            String outputFileName = input.next();

            // scan input file
            File inputFile = new File(inputFileName);
            Scanner read = new Scanner(inputFile);
            // get board size
            int n = read.nextInt();

            // matrix to store numerical board
            int[][] board = new int[n][n];

            // loop through the file
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    String current = read.next();
                    // assign numerical values based on char in file
                    if (current.equals("b")) {
                        board[i][j] = -1;
                    } else if (current.equals("o")) {
                        board[i][j] = -3;
                    } else if (current.equals("e")) {
                        board[i][j] = -2;
                    } else if (current.equals("w")) {
                        board[i][j] = 0;
                    } else {
                        // if it is a numerical constant in file make it = itself
                        board[i][j] = Integer.parseInt(current);
                    }
                }
            }

            // create file writer
            File outputFile = new File(outputFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // create object using board, size, and file writer and solve
            Sudoku s = new Sudoku(board, n, writer);
            s.solve();

            // close scanner and writer
            writer.close();
            input.close();
            read.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private int[][] board; // sudoku board
    private int size; // board size
    private BufferedWriter writer;

    // Constructor of Sudoku class
    public Sudoku(int[][] board, int size, BufferedWriter writer) {
        this.board = board; // set initial board
        this.size = size;
        this.writer = writer;
    }

    // Method solves a given puzzle
    public void solve() { // fill the board starting
        if (fill(0)) // at the beginning
            display(); // if success display board
        else // otherwise failure
            try {
                // output and write error
                writer.write("No solution");
                System.out.println("No solution");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
    }

    // Method fills a board using recursion/backtracking.
    // It fills the board starting at a given location
    private boolean fill(int location) {
        int x = location / size; // find x,y coordinates of
        int y = location % size; // current location
        int value;

        if (location >= size * size) // if location exceeds board
            return true; // whole board is filled

        else if (board[x][y] > 0 || board[x][y] == -1) // if location already has value or is meant to be blank
            return fill(location + 1); // fill the rest of board

        else // otherwise
        {
            int current = board[x][y]; // get current value
            // loop through board
            for (value = 1; value <= size; value++) {
                if (current == 0) {
                    board[x][y] = value; // if blank assign value
                } else if (current == -2 && value % 2 == 0) {
                    board[x][y] = value; // if current is even and value is even assign
                } else if (current == -3 && value % 2 != 0) {
                    board[x][y] = value; // if current is odd and value is odd assign
                }

                if (check(x, y) && fill(location + 1))
                    return true; // if number causes no conflicts and the rest
            } // of board can be filled

            board[x][y] = current; // if none of numbers work then
            return false; // revert the value and backtrack
        }
    }

    // Method checks whether a value at a given location causes any conflicts
    private boolean check(int x, int y) {
        int a, b, i, j;
        // if location is b for blank, return true
        if (board[x][y] == -1) {
            return true;
        }
        // if location is an unchanged e or o, return false
        if (board[x][y] == -2 || board[x][y] == -3) {
            return false;
        }

        for (j = 0; j < size; j++) // check value causes conflict in row
            if (j != y && board[x][j] == board[x][y])
                return false;

        for (i = 0; i < size; i++) // check value causes conflict in column
            if (i != x && board[i][y] == board[x][y])
                return false;

        // Use square root of size to find the nxn regions
        int n = (int) Math.sqrt(size);
        a = (x / n) * n;
        b = (y / n) * n; // check value causes conflict in
        for (i = 0; i < n; i++) // nxn region
            for (j = 0; j < n; j++)
                if ((a + i != x) && (b + j != y) && board[a + i][b + j] == board[x][y])
                    return false;

        return true;
    }

    // Method displays a board and writes to file
    private void display() {
        try {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++)
                    // revert -1 back to b
                    if (board[i][j] == -1) {
                        writer.write("b" + "  | ");
                        System.out.print("b" + "  | ");
                        // single digits get an extra space for formatting purposes
                    } else if (board[i][j] < 10) {
                        writer.write(board[i][j] + "  | ");
                        System.out.print(board[i][j] + "  | ");
                    } else {
                        writer.write(board[i][j] + " | ");
                        System.out.print(board[i][j] + " | ");
                    }
                System.out.println();
                writer.newLine();
                for (int j = 0; j < size; j++) {
                    writer.write("-----");
                    System.out.print("-----");
                }
                System.out.println();
                writer.newLine();

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}