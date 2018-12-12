package Boggle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.function.Consumer;


/**
 * Creates the display for the game board
 * @author Tristin glunt | tglunt@unm.edu | cs.unm.edu/~tglunt
 */
public class GameBoardDisplay implements EventHandler<ActionEvent>
{
    private GridPane gameBoardDisplay;
    private Dice[][] gameboard;
    private int sizeOfGameBoard;
    private Dice lastDieClicked;
    private Button rotateRight;
    private Button rotateLeft;
    private static final double SIZE_OF_ROWS = 100.00;
    private Consumer<Character> addToCurrentWord;

    /**
     * GameBoardDisplay:
     * the main display of our Boggle game. This displays our gameboard, contains the gameboard
     * listeners, and draws our rotate buttons and rotate button listeners.
     * @param gameboard 2D Dice[][] gameboard created by the GameBoard object
     * @param sizeOfGameBoard the size of the gameboard to correctly draw the display
     * @param addToCurrentWord the lambda function passed down from the GUI
     */
    public GameBoardDisplay(Dice[][] gameboard, int sizeOfGameBoard, Consumer<Character> addToCurrentWord)
    {
        this.gameboard = gameboard;
        gameBoardDisplay = new GridPane();
        this.sizeOfGameBoard = sizeOfGameBoard;
        this.addToCurrentWord = addToCurrentWord;
        drawGameBoardDisplay(SIZE_OF_ROWS);
        rotateButtons();

    }

    public GridPane getGameBoardDisplay() { return gameBoardDisplay; }
    public Button getRotateRightButton() { return rotateRight; }
    public Button getRotateLeftButton() { return rotateLeft; }

    /**
     * drawGameBoardDisplay:
     * draws our 2D game board according to the passed down Dice[][] gameboard
     * from the GameBoard object
     * @param sizeOfRows size of the gameboard display
     */
    private void drawGameBoardDisplay(double sizeOfRows)
    {

        gameBoardDisplay.setGridLinesVisible(true);
        gameBoardDisplay.setStyle("-fx-border-color: black");
        gameBoardDisplay.setLayoutX(365);
        gameBoardDisplay.setLayoutY(75);
        gameBoardDisplay.setVgap(5);
        gameBoardDisplay.setHgap(5);

        for(int i = 0; i < sizeOfGameBoard; i++)
        {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth( sizeOfRows / sizeOfGameBoard);
            gameBoardDisplay.getColumnConstraints().add(colConst);
        }
        for(int i = 0; i < sizeOfGameBoard; i++)
        {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight( sizeOfRows /sizeOfGameBoard);
            gameBoardDisplay.getRowConstraints().add(rowConst);
        }

        for(int i = 0; i < sizeOfGameBoard; i++)
        {
            for(int j = 0; j < sizeOfGameBoard; j++)
            {
                gameboard[i][j].setOnAction(this::handle);
                gameBoardDisplay.add(gameboard[i][j], j, i);
            }
        }
    }

    /**
     * rotateButtons:
     * creates our rotate buttons, adds them to display and sets
     * their onAction handler to transpose their correct direction 45deg
     */
    private void rotateButtons()
    {
        rotateRight = new Button("Rotate Right");
        rotateLeft = new Button("Rotate Left");

        rotateRight.setOnAction(event -> {
            transpose45DegreesRight();
            gameBoardDisplay.getChildren().clear();

            for(int i = 0; i < sizeOfGameBoard; i++)
            {
                for(int j = 0; j < sizeOfGameBoard; j++)
                {
                    gameboard[i][j].setRowAndCol(i, j);
                    gameboard[i][j].setOnAction(this::handle);
                    gameBoardDisplay.add(gameboard[i][j], j, i);
                }
            }
        });

        rotateLeft.setOnAction(event -> {
            transport45DegreesLeft();
            gameBoardDisplay.getChildren().clear();

            for(int i = 0; i < sizeOfGameBoard; i++)
            {
                for(int j = 0; j < sizeOfGameBoard; j++)
                {
                    gameboard[i][j].setRowAndCol(i, j);
                    gameboard[i][j].setOnAction(this::handle);
                    gameBoardDisplay.add(gameboard[i][j], j, i);
                }
            }
        });


        rotateRight.setLayoutX(700);
        rotateRight.setLayoutY(600);
        rotateLeft.setLayoutY(600);
        rotateLeft.setLayoutX(450);
    }

    /**
     * handle:
     * listener to the die when they are clicked. This checks whether
     * the selected die is a valid move, highlights the die, and builds onto
     * our word
     * @param event mouse press listener
     */
    public void handle(ActionEvent event)
    {
        Dice currentDieClicked = (Dice)event.getSource();
        int row = gameBoardDisplay.getRowIndex(currentDieClicked);
        int col = gameBoardDisplay.getColumnIndex(currentDieClicked);

        if(!currentDieClicked.getClicked())
        {
            if(isAdjacent(row, col))
            {
                if (lastDieClicked != null)
                {
                    resetHighlitedAdjacent(lastDieClicked.getRow(), lastDieClicked.getCol());
                }
                highlightAdjacent(row, col);

                currentDieClicked.setStyle("-fx-background-color: rgb(1, 174, 229); -fx-font-size: 32; -fx-border-color: black;");
                currentDieClicked.setClicked(true);

                addToCurrentWord.accept(currentDieClicked.getShowingLetterOfDie());

                lastDieClicked = currentDieClicked;
            }
        }
    }

    /**
     * isAdjacent:
     * determines whether or not the given die at (row, col) is adjacent
     * to the last die that was clicked
     * @param row row coordinate of given die
     * @param col col coordinate of given die
     * @return whether or not the given die at (row, col) is adjacent
     */
    private boolean isAdjacent(int row, int col)
    {
        if(lastDieClicked == null) { return true;}
        else
        {
            int lastRow = lastDieClicked.getRow();
            int lastCol = lastDieClicked.getCol();

            if(row < sizeOfGameBoard-1 && col < sizeOfGameBoard-1)
                if(row + 1 == lastRow && col+1 == lastCol )
                    return true;
            if(row < sizeOfGameBoard-1)
                if(row + 1 == lastRow && col == lastCol)
                    return true;
            if(row < sizeOfGameBoard-1 && col > 0)
                if(row + 1 == lastRow && col - 1 == lastCol)
                    return true;
            if(col < sizeOfGameBoard-1)
                if(row == lastRow && col+1 == lastCol)
                    return true;
            if(col > 0)
                if(row == lastRow && col - 1 == lastCol)
                    return true;
            if(row > 0 && col < sizeOfGameBoard-1)
                if(row - 1 == lastRow && col + 1 == lastCol)
                    return true;
            if(row > 0)
                if(row - 1 == lastRow && col == lastCol)
                    return true;
            if(row > 0 && col > 0)
                if(row - 1 == lastRow && col - 1 == lastCol)
                   return true;

            return false;
        }
    }

    /**
     * resetHighlitedAdjacent:
     * reset the highlighted die surrounding the last clicked die. We can not reset the board as cleanly
     * as resetGameboardAfterEnter because we do not want to accidentally remove the highlighted buttons
     * that were clicked
     * @param row row coordinates of last clicked die
     * @param col col coordinates of last clicked die
     */
    public void resetHighlitedAdjacent(int row, int col)
    {
        if(row < sizeOfGameBoard-1)
        {
            if (!gameboard[row + 1][col].getClicked())
                gameboard[row + 1][col].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
            if(col < sizeOfGameBoard-1)
            {
                if (!gameboard[row + 1][col + 1].getClicked())
                    gameboard[row + 1][col + 1].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
            }
            if(col > 0)
            {
                if (!gameboard[row + 1][col - 1].getClicked())
                    gameboard[row + 1][col - 1].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
            }
        }
        if(row > 0)
        {
            if (!gameboard[row - 1][col].getClicked())
                gameboard[row - 1][col].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
            if(col > 0)
            {
                if (!gameboard[row - 1][col - 1].getClicked())
                    gameboard[row - 1][col - 1].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
            }
            if(col < sizeOfGameBoard-1)
            {
                if (!gameboard[row - 1][col + 1].getClicked())
                    gameboard[row - 1][col + 1].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
            }
        }
        if(col < sizeOfGameBoard-1)
        {
            if (!gameboard[row][col + 1].getClicked())
                gameboard[row][col + 1].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
        }
        if(col > 0)
        {
            if (!gameboard[row][col - 1].getClicked())
                gameboard[row][col - 1].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
        }
    }

    /**
     * highlightAdjacent:
     * change the style of the surrounding die of the die given by the
     * arguments row and col
     * @param row row coordinates of clicked die
     * @param col col coordinates of clicked die
     */
    private void highlightAdjacent(int row, int col)
    {
        if(row < sizeOfGameBoard-1)
        {
            if (!gameboard[row + 1][col].getClicked())
                gameboard[row + 1][col].setStyle("-fx-background-color: rgb(179, 236, 255); -fx-font-size: 32; -fx-border-color: black");
            if(col < sizeOfGameBoard-1)
            {
                if (!gameboard[row + 1][col + 1].getClicked())
                    gameboard[row + 1][col + 1].setStyle("-fx-background-color: rgb(179, 236, 255); -fx-font-size: 32; -fx-border-color: black");
            }
            if(col > 0)
            {
                if (!gameboard[row + 1][col - 1].getClicked())
                    gameboard[row + 1][col - 1].setStyle("-fx-background-color: rgb(179, 236, 255); -fx-font-size: 32; -fx-border-color: black");
            }
        }
        if(row > 0)
        {
            if (!gameboard[row - 1][col].getClicked())
                gameboard[row - 1][col].setStyle("-fx-background-color: rgb(179, 236, 255); -fx-font-size: 32; -fx-border-color: black");
            if(col > 0)
            {
                if (!gameboard[row - 1][col - 1].getClicked())
                    gameboard[row - 1][col - 1].setStyle("-fx-background-color: rgb(179, 236, 255); -fx-font-size: 32; -fx-border-color: black");
            }
            if(col < sizeOfGameBoard-1)
            {
                if (!gameboard[row - 1][col + 1].getClicked())
                    gameboard[row - 1][col + 1].setStyle("-fx-background-color: rgb(179, 236, 255); -fx-font-size: 32; -fx-border-color: black");
            }
        }
        if(col < sizeOfGameBoard-1)
        {
            if (!gameboard[row][col + 1].getClicked())
                gameboard[row][col + 1].setStyle("-fx-background-color: rgb(179, 236, 255); -fx-font-size: 32; -fx-border-color: black");
        }
        if(col > 0)
        {
            if (!gameboard[row][col - 1].getClicked())
                gameboard[row][col - 1].setStyle("-fx-background-color: rgb(179, 236, 255); -fx-font-size: 32; -fx-border-color: black");
        }
    }

    /**
     * resetGameBoardAfterEnter:
     * iterate through the game board and ensure every Die is set back to be
     * non-highlighted
     */
    public void resetGameBoardAfterEnter()
    {
        lastDieClicked = null;
        for(int i = 0; i < sizeOfGameBoard; i++)
        {
            for(int j = 0;j < sizeOfGameBoard; j++)
            {
                gameboard[i][j].setClicked(false);
                gameboard[i][j].setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
            }
        }
    }

    /**
     * transport45DegreesRight:
     * rotates the board 45 degrees to the right, used by the rotateRight button
     * from the GUI
     */
    private void transpose45DegreesRight()
    {
        Dice[][] tempArr = new Dice[sizeOfGameBoard][sizeOfGameBoard];

        for(int i = 0; i < sizeOfGameBoard; i++)
        {
            for(int j = 0; j < sizeOfGameBoard; j++)
            {
                tempArr[j][sizeOfGameBoard - 1 - i] = gameboard[i][j];
            }
        }

        gameboard = tempArr;
    }

    /**
     * transport45DegreesLeft:
     * rotates the board 45 degrees to the left, used by the rotateLeft button
     * from the GUI
     */
    private void transport45DegreesLeft()
    {
        Dice[][] tempArr = new Dice[sizeOfGameBoard][sizeOfGameBoard];

        for(int i = 0; i < sizeOfGameBoard; i++)
        {
            for(int j = 0; j < sizeOfGameBoard; j++)
            {
                tempArr[sizeOfGameBoard - 1 - j][i] = gameboard[i][j];
            }
        }

        gameboard = tempArr;
    }

}
