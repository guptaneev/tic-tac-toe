import java.util.Scanner;

/*
Game class is the highest parent class. Contains methods that run the overall game and switch players.
Manages the board, players, and current player.
 */

class Game {
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    // Constructor of Game Object taking in parameters of player1 and player2
    public Game(Player player1, Player player2) {
        this.board = new Board();
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1; // Player 1 starts
    }

    // Method that runs the game
    public void playGame() {
        while (!board.isGameOver()) { // Unless the game ends, print the board and continue making moves
            currentPlayer.makeMove(board);
            board.printBoard();
            if (board.isGameOver()) {
                System.out.println("Game Over! Winner: " + board.getWinner());
                return;
            }
            switchPlayer();
        }
        System.out.println("Game Over! It's a draw.");
    }

    // Alternates the player making the move between player 1 and player 2
    private void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        }
        else {
            currentPlayer = player1;
        }
    }
}

/*
The board class manages the state of the tic-tac-toe board.
It contains methods like making moves, checking if there is a winner, checking if the board is full,
and printing the board and grid (2-D Array of the board).
 */

class Board {
    private char[][] grid;
    private static final int SIZE = 3;

    // Default constructor with no parameters. Creates a 3x3 2-D Array and fills it with '-'.
    public Board() {
        grid = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = '-';
            }
        }
    }

    /* Updates the board with the selected move. Takes in parameters row, column, and symbol, and updates the grid.
    Input validation if the location is already taken up or is not on the board.
    Returns true if the move is completed and false if not.
     */

    public boolean makeMove(int row, int col, char symbol) {
        if (row > 2 || row < 0 || col > 2 || col < 0) {
            System.out.println("Out of bounds!");
            return false;
        }
        if (grid[row][col] == '-') {
            grid[row][col] = symbol;
            return true;
        }
        return false;
    }

    // Checks if there is a winner or if the board is full, signifying an ended game. Returns true if the game is over, false if not.
    public boolean isGameOver() {
        return (hasWinner() || isBoardFull());
    }

    /* Iterates through the entire grid checking for win conditions.
    Returns a character representing the winner of the game. Returns '-' if there is no winner.
     */
    public char getWinner() {

        for (int i = 0; i < SIZE; i++) {

            // Checking horizontal/row wins
            if (grid[i][0] == grid[i][1] && grid[i][1] == grid[i][2] && grid[i][0] != '-') {
                return grid[i][0];
            }

            // Checking vertical/column wins
            if (grid[0][i] == grid[1][i] && grid[1][i] == grid[2][i] && grid[0][i] != '-') {
                return grid[0][i];
            }
        }

        // Checking diagonal wins
        if (grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2] && grid[0][0] != '-') {
            return grid[0][0];
        }
        if (grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0] && grid[0][2] != '-') {
            return grid[0][2];
        }
        return '-';
    }

    /* Checks result of getWinner() to verify if the game has a winner or is a tie.
    Returns 'X' for an X win, 'O' for an O win, or '-' for a tie.
     */
    public boolean hasWinner() {
        return !(getWinner() == '-');
    }

    /* Iterates through grid checking if there are blank squares.
    Returns false if at least one square is blank, true otherwise.
     */
    public boolean isBoardFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    // Iterates through grid and prints the current state of the board.
    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Returns the current state of the grid from a specified board object
    public char[][] getGrid() {
        return grid;
    }

    // Returns the size of the board object. This value is always 3.
    public int getSize() {
        return SIZE;
    }
}

/* This is the abstract player class. This is the parent class of the HumanPlayer class and Bot class.
    This class manages the symbol for each player and has the abstract method makeMove.
 */
abstract class Player {
    char symbol;

    // Overloaded constructor Player, assigning the object a symbol
    public Player(char symbol) {
        this.symbol = symbol;
    }

    /* Abstract method that makes a move. We use an abstract method because the humanPlayer class and Bot class
     have different implementations of makeMove, however they will both use a board as a parameter.
     */
    public abstract void makeMove(Board board);

    // Getter method for the symbol of a player object. Returns the symbol character.
    public char getSymbol() {
        return symbol;
    }
}


//Class that represents the human player. Contains methods for taking input of moves and playing the human moves. Subclass of PLayer.

class HumanPlayer extends Player {
    private Scanner scanner;

    // Constructor of human player, taking in one parameter symbol. Uses this parameter to refer to the super class. Creates scanner object.
    public HumanPlayer(char symbol) {
        super(symbol);
        scanner = new Scanner(System.in);
    }

    // Override makeMove method, taking in one parameter board. User inputs the move.
    @Override
    public void makeMove(Board board) {
        int row;
        int col;
        boolean validMove;
        do {
            System.out.println("Enter your move (row and column) - Example: 2 2 ");
            row = scanner.nextInt();
            col = scanner.nextInt();
            validMove = board.makeMove(row, col, symbol);
            if (!validMove || row > 2 || row < 0 || col > 2 || col < 0) {
                System.out.println("Invalid move. Try again.");
            }
        } while (!validMove);
    }
}

/*
Bot class is a subclass of Player. Represents the bot player that makes the best move given a board.
Contains method to calculate this best move using min-max algorithm and backtracking, as well as overriden method makeMove.
 */

class Bot extends Player {

    // Constructor of Bot, taking in one parameter symbol, sending it to the superclass Player.
    public Bot(char symbol) {
        super(symbol);
    }

    // Override method of makeMove. Calls Board method makeMove using row and column of the best move and symbol for the bot.
    @Override
    public void makeMove(Board board) {
        Move bestMove = findBestMove(board);
        board.makeMove(bestMove.getRow(), bestMove.getCol(), symbol);
    }

    /*
    Method that calculates the best move for any given board. Uses recursion in conjunction with minimax algorithm
    to calculate best move.
     */
    private Move findBestMove(Board board) {
        char[][] grid = board.getGrid();
        int bestScore = Integer.MIN_VALUE; // Sets best value to minimum value
        Move bestMove = new Move(-1, -1); // Temporary best move

        // Iterates through grid and finds all blank spaces
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (grid[i][j] == '-') {
                    grid[i][j] = symbol; // Potential move
                    int moveScore = minimax(board, 0, false); // Computes score for this potential move using minimax
                    grid[i][j] = '-'; // Resets board square
                    if (moveScore > bestScore) { // If this move yields a higher score, it becomes our best move
                        bestMove = new Move(i, j);
                        bestScore = moveScore;
                    }
                }
            }
        }
        return bestMove;
    }

    /*
    Minimax algorithm that takes in parameters board, depth, and isMaximizing
    Depth represents how many moves in the future the algorithm is calculating
    isMaximizing represents if the bot is playing with its own pieces (true) or the opponents piece (false)
     */

    private int minimax(Board board, int depth, boolean isMaximizing) {
        char winner = board.getWinner();
        if (winner == symbol) { // Base case for recursion - the bot wins
            return 10 - depth; // Returns a higher score for fewer moves taken to win
        }
        if (winner != '-' && winner != symbol) { // Base case for recursion - the bot losing
            return depth - 10; // Returns a negative score when the bot loses
        }
        if (board.isBoardFull()) { // Base case for recursion - a tie
            return 0; // Returns 0 score for a tie
        }

        if (isMaximizing) { // The bot playing with its own pieces
            int maxEval = Integer.MIN_VALUE;
            // Iterates through board and finds all blank spaces
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    if (board.getGrid()[i][j] == '-') {
                        board.getGrid()[i][j] = symbol;
                        int eval = minimax(board, depth + 1, false); // Recursively calls minimax again on the new set of potential moves. Sets isMaximizing false to represent opponents moves. Increases depth by 1.
                        board.getGrid()[i][j] = '-';
                        maxEval = Math.max(maxEval, eval);
                    }
                }
            }
            return maxEval;
        } else { // The bot simulating games with the opponent's pieces
            int minEval = Integer.MAX_VALUE;
            char opponentSymbol = '-';
            if (symbol == 'X') {
                 opponentSymbol = 'O';
            }
            else {
                opponentSymbol = 'X';
            }
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    if (board.getGrid()[i][j] == '-') {
                        board.getGrid()[i][j] = opponentSymbol;
                        int eval = minimax(board, depth + 1, true);
                        board.getGrid()[i][j] = '-';
                        minEval = Math.min(minEval, eval);
                    }
                }
            }
            return minEval;
        }
    }
}

/*
Move class represents an individual move on the board. Contains a row and a column. Getter methods for row and column as well.
 */

class Move {
    private int row;
    private int col;

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}

public class Main {
    public static void main(String[] args) {
        Player human = new HumanPlayer('X');
        Player bot = new Bot('O');
        Game game = new Game(human, bot);
        game.playGame();
    }
}

