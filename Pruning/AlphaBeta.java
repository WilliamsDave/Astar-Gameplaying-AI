import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

//This program plays tic-tac point game using min-max, depth limit, board evaluation, and alpha-beta pruning
public class AlphaBeta {

    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in);
            String outputFileName;

            // get user input for board size and output file
            System.out.println("Please enter an even number for board size: ");
            int n = input.nextInt();
            System.out.println("Enter output file name:");
            outputFileName = input.next();

            // create writer
            File outputFile = new File(outputFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // create object using size and writer and start program
            AlphaBeta a = new AlphaBeta(n, writer);
            a.play();

            // close scanner and writer
            writer.close();
            input.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private final char EMPTY = ' '; // empty slot
    private final char COMPUTER = 'X'; // computer
    private final char PLAYER = '0'; // player
    private final int MIN = 0; // min level
    private final int MAX = 1; // max level
    private int LIMIT = 9; // depth limit

    // Board class (inner class)
    private class Board {
        private char[][] array; // board array

        // Constructor of Board class
        private Board(int size) {
            array = new char[size][size]; // create array

            for (int i = 0; i < size; i++) // fill array with empty slots
                for (int j = 0; j < size; j++)
                    array[i][j] = EMPTY;
        }
    }

    private Board board; // game board
    private int size; // size of board
    private BufferedWriter writer;

    // Constructor of AlphaBeta class
    public AlphaBeta(int size, BufferedWriter writer) {
        this.board = new Board(size); // create game board
        this.size = size; // set board size
        this.writer = writer; // set writer
    }

    // Method plays game
    public void play() {
        // set lower limit for larger board
        if (size > 4) {
            LIMIT = 6;
        }
        while (!boardFull(board)) // computer and player take turns while board is not full
        {
            // player makes a move
            board = playerMove(board);

            if (!boardFull(board))// if board still isn't full computer makes move
            {
                board = computerMove(board);
            }

            // if board is full then finalize game
            if (boardFull(board)) {
                try {
                    // calculate player and computer score
                    int compScore = calcScore(board, COMPUTER);
                    int playerScore = calcScore(board, PLAYER);
                    // display and write scores
                    writer.write("Player: " + playerScore + " Computer: " + compScore + "\n");
                    System.out.println("Player: " + playerScore + " Computer: " + compScore + "\n");

                    // check who won or if it was draw and display result
                    if (compScore > playerScore) {
                        writer.write("Computer Wins");
                        System.out.println("Computer Wins");
                    } else if (playerScore > compScore) {
                        writer.write("Player Wins");
                        System.out.println("Player Wins");
                    } else {
                        writer.write("Draw");
                        System.out.println("Draw");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    // Method lets the player make a move
    private Board playerMove(Board board) {
        try {
            // player move header
            writer.write("Player move: \n");
            System.out.println("Player move:");

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in); // read player's move
        int i = scanner.nextInt();
        int j = scanner.nextInt();

        board.array[i][j] = PLAYER; // place player symbol

        displayBoard(board); // diplay board

        return board; // return updated board
    }

    // Method determines computer's move
    private Board computerMove(Board board) { // generate children of board
        LinkedList<Board> children = generate(board, COMPUTER);

        int maxIndex = -1;
        int maxValue = Integer.MIN_VALUE;
        // find the child with
        for (int i = 0; i < children.size(); i++) // largest minmax value
        {
            int currentValue = minmax(children.get(i), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (currentValue > maxValue) {
                maxIndex = i;
                maxValue = currentValue;
            }
        }
        Board result = children.get(maxIndex); // choose the child as next move
        try {
            // computer move header
            writer.write("Computer move: \n");
            System.out.println("Computer move:");

        } catch (IOException e) {
            e.printStackTrace();
        }

        displayBoard(result); // print next move

        return result; // retun updated board
    }

    private int calcScore(Board board, char player) {
        // 2 in a row and 3 in a row trackers
        int p = 0;
        int q = 0;
        // loop through each item checking down and to the right only
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // if square matches which player (cpu or player)
                if (board.array[i][j] == player) {
                    if (i < size - 2) {// room to check for 3 in a row vertically
                        if (board.array[i][j] == board.array[i + 1][j] && board.array[i][j] == board.array[i + 2][j]) {
                            q++; // if all 3 are the same increment counter
                        }

                    }
                    if (i < size - 1) {// room to check for 2 in a row vertically
                        if (board.array[i][j] == board.array[i + 1][j]) {
                            p++; // if both are the same increment counter
                        }
                    }
                    if (j < size - 2) {// room to check for 3 in a row horizontally
                        if (board.array[i][j] == board.array[i][j + 1]
                                && board.array[i][j] == board.array[i][j + 2]) {
                            q++; // if all 3 are the same increment counter
                        }
                    }
                    if (j < size - 1) {// room to check for 2 in a row horizontally
                        if (board.array[i][j] == board.array[i][j + 1]) {
                            p++; // if both are the same increment counter
                        }
                    }

                }
            }
        }
        // calculate points based on counters
        return ((2 * p) + (3 * q));
    }

    // checks whether the board is full yet
    private boolean boardFull(Board board) {
        boolean full = true;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.array[i][j] == EMPTY) {
                    full = false; // flags board as not full if empty spot found
                }
            }
        }
        return full;
    }

    // custom heuristic based on points, getting central control, and forming
    // squares
    private int heuristicEval(Board board) {
        int score = calcScore(board, COMPUTER) - calcScore(board, PLAYER);

        // central control heuristic
        if (size >= 4) {
            for (int i = size / 2 - 1; i <= size / 2; i++) {
                for (int j = size / 2 - 1; j <= size / 2; j++) {
                    // if computer has a shape in the center score increases
                    if (board.array[i][j] == COMPUTER) {
                        score += 15;
                        // if player has a shape in the center score decreases
                    } else if (board.array[i][j] == PLAYER) {
                        score -= 15;
                    }
                }
            }
        }

        // encourage the creation and blocking of squares which earn more points because
        // of overlaps
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                // if player is forming a square lose points
                if (board.array[i][j] == PLAYER && board.array[i][j + 1] == PLAYER &&
                        board.array[i + 1][j] == PLAYER && board.array[i + 1][j + 1] == PLAYER) {
                    score -= 10;
                }
                // if computer is forming square increase points
                if (board.array[i][j] == COMPUTER && board.array[i][j + 1] == COMPUTER &&
                        board.array[i + 1][j] == COMPUTER && board.array[i + 1][j + 1] == COMPUTER) {
                    score += 10;
                }
            }
        }

        // return score that takes into account the actual score of each player,
        // the central control, and the creation of squares
        return score;
    }

    // Method computes minmax value of a board
    private int minmax(Board board, int level, int depth, int alpha, int beta) {
        // calculate scores for each player
        int compScore = calcScore(board, COMPUTER);
        int playerScore = calcScore(board, PLAYER);

        // if board is an end point
        if (boardFull(board)) {
            if (compScore > playerScore) {
                return 100; // large reward for game won
            } else if (playerScore > compScore) {
                return -100; // large negative points for losing game
            } else {
                return 0; // draws are worth nothing
            }
            // if we reach depth limit before end of game
        } else if (depth >= LIMIT) {
            // use custom heuristic in this case
            return heuristicEval(board);
        } else {
            if (level == MAX) // if board is at max level
            {
                LinkedList<Board> children = generate(board, COMPUTER);
                // generate children of board
                int maxValue = Integer.MIN_VALUE;

                for (int i = 0; i < children.size(); i++) { // find minmax values of children
                    int currentValue = minmax(children.get(i), MIN, depth + 1, alpha, beta);

                    if (currentValue > maxValue) // find maximum of minmax values
                        maxValue = currentValue;

                    if (maxValue >= beta) // if maximum exceeds beta stop
                        return maxValue;

                    if (maxValue > alpha) // if maximum exceeds alpha update alpha
                        alpha = maxValue;
                }

                return maxValue; // return maximum value
            } else // if board is at min level
            {
                LinkedList<Board> children = generate(board, PLAYER);
                // generate children of board
                int minValue = Integer.MAX_VALUE;

                for (int i = 0; i < children.size(); i++) { // find minmax values of children
                    int currentValue = minmax(children.get(i), MAX, depth + 1, alpha, beta);

                    if (currentValue < minValue) // find minimum of minmax values
                        minValue = currentValue;

                    if (minValue <= alpha) // if minimum is less than alpha stop
                        return minValue;

                    if (minValue < beta) // if minimum is less than beta update beta
                        beta = minValue;
                }

                return minValue; // return minimum value
            }
        }
    }

    // Method generates children of board using a symbol
    private LinkedList<Board> generate(Board board, char symbol) {
        LinkedList<Board> children = new LinkedList<Board>();
        // empty list of children
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) // go thru board
                if (board.array[i][j] == EMPTY) { // if slot is empty
                    Board child = copy(board); // put the symbol and
                    child.array[i][j] = symbol; // create child board
                    children.addLast(child);
                }

        return children; // return list of children
    }

    // Method makes copy of a board
    private Board copy(Board board) {
        Board result = new Board(size);

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                result.array[i][j] = board.array[i][j];

        return result;
    }

    // Method displays a board
    private void displayBoard(Board board) {
        try {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    // write the display to file as well
                    writer.write(" " + board.array[i][j] + " ");
                    System.out.print(" " + board.array[i][j] + " ");

                    if (j < size - 1) {
                        // create vertical tic tac toe border
                        writer.write("|");
                        System.out.print("|");
                    }
                }

                writer.newLine();
                System.out.println();

                if (i < size - 1) {
                    // create vertical tic tac toe border
                    writer.write("---");
                    System.out.print("---");
                    for (int j = 1; j < size; j++) {
                        writer.write("----");
                        System.out.print("----");
                    }
                    writer.newLine(); // Move to the next line
                    System.out.println(); // Move to the next line
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}