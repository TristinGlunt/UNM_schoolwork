/*
    Tristin Glunt 5/26/2017 practice Gomoku
    in prep for CS 351 at UNM
 */
import cs251.lab3.GomokuModel;
import cs251.lab3.GomokuGUI;

public class Gomoku implements GomokuModel {
    private int computerFlag = 0;
    int winningPlayer = -1;
    Player player1 = new Player();
    Player player2 = new Player();
    private Square[][] gameBoard = new Square[DEFAULT_NUM_ROWS][DEFAULT_NUM_COLS];

    public static void main (String[] args ) {
        Gomoku game = new Gomoku();
        if ( args.length > 0) {
            game.setComputerPlayer(args[0]);
        }
        GomokuGUI.showGUI(game);
    }

    public int getNumRows() {
        return DEFAULT_NUM_ROWS;
    }

    public int getNumCols() {
        return DEFAULT_NUM_COLS;
    }

    public int getNumInLineForWin() {
        return SQUARES_IN_LINE_FOR_WIN;
    }

    public void setComputerPlayer(String s) {
        if(s == null) {
            computerFlag = 0;
        } else if(s.equals("Yes") || s.equals("YES") || s.equals("Y")) {
            computerFlag = 1;
        } else {
            computerFlag = 0;
        }
    }

    /*
    currently checking for win in horizontal direction for rings only
    checks by getting current column position and checking in 5 units in
    both directions, if 5 trues in a row then the method will return true
     */
    //TODO Check whether or not the symbol is on the edge for each check, DONT GO PAST EDGE
    //TODO HORIZONTAL CHECK IS WORKING ON EDGE NOW, DIAGONAL AND ROW CHECK ARE NOT
    private boolean checkHorizontal(int row, int col, Square sym) {
        boolean horizontalWin = false;
        int lastCol, beginCol;
        int inARow = 0;
        if(col < 25 && col > 5) {
            lastCol = col + 5;
            beginCol = col - 5;
        } else if(col > 25) {
            lastCol = 30;
            beginCol = col - 5;
        } else { // col < 5
            lastCol =  col + 5;
            beginCol = 0;
        }

        while(beginCol < lastCol) {
            if (gameBoard[row][beginCol] == sym) {
                inARow++;
                if(inARow == 5) {
                    return true;
                }
                beginCol++;
            } else {
                inARow = 0;
                beginCol++;
            }
        }
        return false;
    }

    private boolean checkVerticle(int row, int col, Square sym) {
        boolean verticleWin = false;
        int inACol = 0;
        int lastRow = row+5;
        int beginRow = row-5;

        while(beginRow < lastRow) {
            if (gameBoard[beginRow][col] == sym) {
                inACol++;
                if(inACol == 5) {
                    return true;
                }
                beginRow++;
            } else {
                inACol = 0;
                beginRow++;
            }
        }
        return false;
    }

    //TODO look at combining checkDiagonals, only difference is the direction of column checking
    //TODO (i.e. one starts from the left and the other starts from the right)
    // currently added what I thought would fix array index out of bounds, not wokring
    private boolean checkDiagonalLR(int row, int col, Square sym) {
        boolean diagonalWin = false;
        int lastDiagRow, beginDiagRow, lastDiagCol, beginDiagCol;
        int inADiag = 0;
        if(row < 25 && row > 5 && col < 25 && col > 5) {
            lastDiagRow = row + 5;
            beginDiagRow = row - 5;
            lastDiagCol = col + 5;
            beginDiagCol = col - 5;
        } else if(col > 25) {
            lastDiagRow = row + 5;
            beginDiagRow = row - 5;
            lastDiagCol = 30;
            beginDiagCol = col - 5;
        } else {
            lastDiagRow = row + 5;
            beginDiagRow = row - 5;
            lastDiagCol = col + 5;
            beginDiagCol = 0;
        }

        while(beginDiagRow < lastDiagRow && beginDiagCol < lastDiagCol) {
            if (gameBoard[beginDiagRow][beginDiagCol] == sym) {
                inADiag++;
                if(inADiag == 5) {
                    return true;
                }
                beginDiagRow++;
                beginDiagCol++;
            } else {
                inADiag = 0;
                beginDiagRow++;
                beginDiagCol++;
            }
        }
        return false;
    }
// currently added what I thought would fix array index out of bounds, not wokring
    private boolean checkDiagonalRL(int row, int col, Square sym) {
        boolean diagonalWin = false;
        int inADiag = 0;
        int lastDiagRow, beginDiagRow, lastDiagCol, beginDiagCol;
        if(row > 5 && row < 25 && col < 25 && col > 5) {
            lastDiagRow = row+5;
            beginDiagRow = row-5;
            lastDiagCol = col-5;
            beginDiagCol= col+5;
        } else if(col > 25) {
            lastDiagRow = row + 5;
            beginDiagRow = row-5;
            lastDiagCol = col-5;
            beginDiagCol= 30;
        } else {
            lastDiagRow = row + 5;
            beginDiagRow = row-5;
            lastDiagCol = 0;
            beginDiagCol= col+5;
        }


        while(beginDiagRow < lastDiagRow) {
            if (gameBoard[beginDiagRow][beginDiagCol] == sym) {
                inADiag++;
                if(inADiag == 5) {
                    return true;
                }
                beginDiagRow++;
                beginDiagCol--;
            } else {
                inADiag = 0;
                beginDiagRow++;
                beginDiagCol--;
            }
        }
        return false;
    }

    //If every check returns true, a win has happened! otherwise return false
    private boolean winDetection(int row, int col, Square sym) {
        if ((checkHorizontal(row, col, sym)) || checkVerticle(row, col, sym) || checkDiagonalLR(row, col, sym) ||
                checkDiagonalRL(row, col, sym)) {
            return true;
        } else {
            return false;
        }
    }
    /*Get a string representation of the board. The characters are given by the Square.toChar method, so a hyphen in
    the string is an empty square, \an uppercase 'O' represents the ring, and uppercase 'X' is the cross. Each line in the
    board is separated by a new line character '\n'. An example of a small board might look like this if viewed in a text file:
    OX--X
    XOXOO
    XXOOX
    OOXOX
    XOXOO
    In this case, O must have started, and just won the game through a diagonal.
    */
    public String getBoardString() {
        //maybe build single array to hold all values?
        //char[][] boardString = new char[30][30];
        StringBuilder builder = new StringBuilder();

        for(int row = 0; row < getNumRows(); row++) {
            for(int col = 0; col < getNumCols(); col++) {
                builder.append(gameBoard[row][col].toChar());
            }
            builder.append('\n');
        }
        String printableBoard = builder.toString();
        System.out.println(printableBoard);
        return printableBoard;
    }

    /*
    Attempt a move at a given location on the board. This method is called when the user clicks in the board.
    If the square is already occupied, nothing about the state of the game changes.
    If however, an empty square is clicked, then it should be filled with a value representing the player currently in turn.
    On a failure, the turn doesn't change, but that player gets to go again. Called from the GUI.

     Also, currently only checking for .RING win detection, gameOver will return whatever winDetection finds out
     depending on if gameOver is true or not, also only returning a .RING_WINS
     */
    public GomokuModel.Outcome playAtLocation(int row, int col) {
        boolean gameRing = false;
        boolean gameCross = false;
        if(gameBoard[row][col] == Square.EMPTY) {
            if (player1.getCurrentTurn()) {
                System.out.println("SHOULD PLACE CROSS");
                gameBoard[row][col] = player1.getPlayerSymbol();
                gameCross= winDetection(row, col, player1.getPlayerSymbol());
                player1.setCurrentTurn(false);
                player2.setCurrentTurn(true);
            } else {
                System.out.println("SHOULD PLACE RING");
                gameBoard[row][col] = player2.getPlayerSymbol();
                gameRing = winDetection(row, col, player2.getPlayerSymbol());
                player1.setCurrentTurn(true);
                player2.setCurrentTurn(false);
            }
        }

        if(computerFlag == 1){
            playComputer(row, col);
            player1.setCurrentTurn(true);
            player2.setCurrentTurn(false);
            gameRing = winDetection(row, col, player2.getPlayerSymbol());
        }

        if(gameRing) {
            return Outcome.RING_WINS;
        } else if(gameCross) {
            return Outcome.CROSS_WINS;
        } else {
            return Outcome.GAME_NOT_OVER;
        }
    }

    public void playComputer(int lastRow, int lastCol) {
        gameBoard[lastRow+1][lastCol] = player2.getPlayerSymbol();
    }

    /*Starts a new game, resets the game board to empty. Pick a random player to go first. In the expected part of the program,
    you should make it so that the player who won the last game gets to go first in the next round.
    This method is called by the GUI whenever a new game is supposed to be started, this includes before the first game.
     */
    public void startNewGame() {
        double ranNum = Math.random();
        player1.setPlayerSymbol(Square.CROSS);
        player2.setPlayerSymbol(Square.RING);

        initGameArray();

        if(ranNum > 0.50) {
            player1.setCurrentTurn(true);
        } else {
            player2.setCurrentTurn(true);
        }
    }

    private void initGameArray() {
        for(int row = 0; row < getNumRows(); row++) {
            for(int col = 0; col < getNumCols(); col++) {
                gameBoard[row][col] = Square.EMPTY;
            }
        }
    }
}