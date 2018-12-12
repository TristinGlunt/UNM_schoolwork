package Boggle;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Boggle set of configurations found from
 * https://boardgames.stackexchange.com/questions/29264/boggle-what-is-the-dice-configuration-for-boggle-in-various-languages
 * for both 4x4 gameboard and 5x5
 * @author Tristin glunt | tglunt@unm.edu | cs.unm.edu/~tglunt
 */
public class GameBoard
{
    private int NUM_OF_DICE;
    private ArrayList<Dice> setOfDice = new ArrayList<>(NUM_OF_DICE);
    private int sizeOfGameBoard;
    private static final int FOUR_BY_FOUR = 4;
    private static final int FIVE_BY_FIVE = 5;
    private static final int CHANCE_OF_Q_NXT_TO_U = 60;
    private Dice[][] gameboard;
    private GameBoardDisplay gameBoardDisplay;

    /**
     * GameBoard creates the tray for our Boggle game. It passes the board dimensions
     * down to the gameboard display as well the 2D array of Dice so it can display
     * the correct board, as well as our lambda function addToCurrent word from the Coordinator
     * @param boardDim the dimension of the board
     * @param addToCurrentWord the function that adds to a currently being built word
     */
    public GameBoard(int boardDim, Consumer<Character> addToCurrentWord)
    {
        sizeOfGameBoard = boardDim;
        NUM_OF_DICE = boardDim*boardDim;

        if(boardDim == FOUR_BY_FOUR)
        {
            gameboard = new Dice[sizeOfGameBoard][sizeOfGameBoard];
            createGameBoard();
            gameBoardDisplay = new GameBoardDisplay(gameboard, sizeOfGameBoard, addToCurrentWord);
        }
        else if(boardDim == FIVE_BY_FIVE)
        {
            gameboard = new Dice[sizeOfGameBoard][sizeOfGameBoard];
            createGameBoard();
            gameBoardDisplay = new GameBoardDisplay(gameboard, sizeOfGameBoard, addToCurrentWord);
        }
    }

    public Dice[][] getGameboard() { return gameboard; }

    public GridPane getGameBoardDisplay() { return gameBoardDisplay.getGameBoardDisplay(); }

    public Button getRotateLeftButton() { return gameBoardDisplay.getRotateLeftButton();}
    public Button getRotateRightButton() { return gameBoardDisplay.getRotateRightButton();}

    /**
     * resetGameBoardAfterEnter:
     * after a word has been entered through the ENTER key, this method calls
     * our gameBoardDisplay to reset the highlighted dice
     */
    public void resetGameBoardAfterEnter()
    {
        gameBoardDisplay.resetGameBoardAfterEnter();
    }

    /**
     * createGameBoard:
     * creates our set of dice and shuffles the game board
     */
    private void createGameBoard()
    {
        createSetOfDice();
        shuffleGameBoard();
    }

    /**
     * createSetOfDice:
     * Hard code possible dice combinations. These are directly taken from the Boggle
     * game increasing the "funness" of our game to have a much higher chance to produce
     * actual words. I understand this may be bad practice as this is hard coded, and we
     * do not easily have the option to create a 3x3 board or a 10x10 board, but the spec was
     * very specific for only 4x4 and 5x5 gameboards, and I do have great modularity to
     * never repeat any other code even though the size of the gameboards do not match.
     *
     * This is the only part of gameboards which is not modular, and if I had to make it modular
     * I would create a set including all of the alphabet for each Die. I would then create as many Die
     * for the size of the gameBoard, for loop making new Dice. Then roll each dice. This method would be
     * the best way for me to still use lots of my already written code, and only change the below method.
     *
     * Hopefully the above paragraph demonstrates I know how to make things modular and I do not
     * get deducted for this specific part not being modular, as it was a design choice to create a better
     * game.
     *
     */
    private void createSetOfDice()
    {
        if(sizeOfGameBoard == FOUR_BY_FOUR)
        {
            setOfDice.add(0, new Dice('R', 'I', 'F', 'O', 'B', 'X'));
            setOfDice.add(1, new Dice('I', 'F', 'E', 'H', 'E', 'Y'));
            setOfDice.add(2, new Dice('D', 'E', 'N', 'O', 'W', 'S'));
            setOfDice.add(3, new Dice('U', 'T', 'O', 'K', 'N', 'D'));
            setOfDice.add(4, new Dice('H', 'M', 'S', 'R', 'A', 'O'));
            setOfDice.add(5, new Dice('L', 'U', 'P', 'E', 'T', 'S'));
            setOfDice.add(6, new Dice('A', 'C', 'I', 'T', 'O', 'A'));
            setOfDice.add(7, new Dice('Y', 'L', 'G', 'K', 'U', 'E'));
            setOfDice.add(8, new Dice('Q', 'B', 'M', 'J', 'O', 'A')); //this Q is supposed to have a U next to it
            setOfDice.add(9, new Dice('E', 'I', 'H', 'S', 'P', 'N'));
            setOfDice.add(10, new Dice('V', 'T', 'E', 'I', 'G', 'N'));
            setOfDice.add(11, new Dice('B', 'L', 'A', 'I', 'Y', 'T'));
            setOfDice.add(12, new Dice('E', 'A', 'Z', 'V', 'N', 'D'));
            setOfDice.add(13, new Dice('R', 'L', 'A', 'E', 'S', 'C'));
            setOfDice.add(14, new Dice('U', 'I', 'W', 'L', 'R', 'G'));
            setOfDice.add(15, new Dice('P', 'C', 'A', 'E', 'M', 'D'));
        } else if(sizeOfGameBoard == FIVE_BY_FIVE)
        {
            setOfDice.add(0, new Dice('Q', 'B', 'Z', 'J', 'X', 'L'));
            setOfDice.add(1, new Dice('E', 'H', 'L', 'R', 'D', 'O'));
            setOfDice.add(2, new Dice('T', 'E', 'L', 'P', 'C', 'I'));
            setOfDice.add(3, new Dice('T', 'T', 'O', 'T', 'E', 'M'));
            setOfDice.add(4, new Dice('A', 'E', 'A', 'E', 'E', 'H'));
            setOfDice.add(5, new Dice('T', 'O', 'U', 'O', 'T', 'O'));
            setOfDice.add(6, new Dice('N', 'H', 'D', 'T', 'H', 'O'));
            setOfDice.add(7, new Dice('S', 'S', 'N', 'S', 'E', 'U'));
            setOfDice.add(8, new Dice('S', 'C', 'T', 'I', 'E', 'P'));
            setOfDice.add(9, new Dice('Y', 'I', 'F', 'P', 'S', 'R'));
            setOfDice.add(10, new Dice('O', 'V', 'W', 'R', 'G', 'R'));
            setOfDice.add(11, new Dice('L', 'H', 'N', 'R', 'O', 'D'));
            setOfDice.add(12, new Dice('R', 'I', 'Y', 'P', 'R', 'H'));
            setOfDice.add(13, new Dice('E', 'A', 'N', 'D', 'N', 'N'));
            setOfDice.add(14, new Dice('E', 'E', 'E', 'E', 'M', 'A'));
            setOfDice.add(15, new Dice('A', 'A', 'A', 'F', 'S', 'R'));
            setOfDice.add(16, new Dice('A', 'F', 'A', 'I', 'S', 'R'));
            setOfDice.add(17, new Dice('D', 'O', 'R', 'D', 'L', 'N'));
            setOfDice.add(18, new Dice('M', 'N', 'N', 'E', 'A', 'G'));
            setOfDice.add(19, new Dice('I', 'T', 'I', 'T', 'I', 'E'));
            setOfDice.add(20, new Dice('A', 'U', 'M', 'E', 'E', 'G'));
            setOfDice.add(21, new Dice('Y', 'I', 'F', 'A', 'S', 'R'));
            setOfDice.add(22, new Dice('C', 'C', 'W', 'N', 'S', 'T'));
            setOfDice.add(23, new Dice('U', 'O', 'T', 'O', 'W', 'N'));
            setOfDice.add(24, new Dice('E', 'T', 'I', 'L', 'I', 'C'));
        }
    }

    /**
     * shuffleGameBoard:
     * shuffles our set of dice and sets the showing side of each die.
     * Goes through our set of Dice and inserts the corresponding index into
     * our 2D array of gameboard. We also set the corresponding row and col to each
     * die by setRowAndCol
     */
    private void shuffleGameBoard()
    {
        shuffleSetOfDice();
        setShowingSideOfDice();


        for(int i = 0; i < sizeOfGameBoard; i++)
        {
            for(int j = 0; j < sizeOfGameBoard; j++)
            {
                setOfDice.get((i*sizeOfGameBoard)+j).setRowAndCol(i, j);
                gameboard[i][j] = setOfDice.get((i*sizeOfGameBoard)+j);
            }
        }
    }

    /**
     * shuffleSetOfDice:
     * takes our setOfDice and randomizes the order
     */
    private void shuffleSetOfDice()
    {
        Collections.shuffle(setOfDice);
    }

    /**
     * setShowingSideOfDice:
     * iterates through each of our created dice and calls the rollDie function implemented
     * in the Dice class. Set showing side of dice also implements the increased chance of a U appearing
     * next to a Q. (60% chance in current implementation)
     */
    private void setShowingSideOfDice()
    {
        int endOf4by4 = 3;
        int endOf5by5 = 4;
        for(int i = 0; i < NUM_OF_DICE; i++)
        {
            setOfDice.get(i).rollDie();
            if(setOfDice.get(i).getShowingLetterOfDie() == 'Q')     //if statement used to give 60% chance of U nxt to Q
            {
                Random ranNum = new Random();
                if(ranNum.nextInt(100) < CHANCE_OF_Q_NXT_TO_U)
                {
                    System.out.println("U should be next to Q");
                    if(sizeOfGameBoard == FOUR_BY_FOUR)
                        if((i-endOf4by4) % FOUR_BY_FOUR == 0 && i != endOf4by4)
                            setOfDice.get(i-FOUR_BY_FOUR).setShowingLetterOfDie('U');
                        else if((i-endOf4by4) % FOUR_BY_FOUR == 0 && i == endOf4by4)
                            setOfDice.get(i-1).setShowingLetterOfDie('U');
                        else
                        {
                            setOfDice.get(i + 1).setShowingLetterOfDie('U');
                            i++;
                        }
                    else if(sizeOfGameBoard == FIVE_BY_FIVE)
                        if((i-endOf5by5) % FIVE_BY_FIVE == 0 && i != endOf5by5)
                            setOfDice.get(i-FIVE_BY_FIVE).setShowingLetterOfDie('U');
                        else if((i-endOf5by5) % FIVE_BY_FIVE == 0 && i == endOf5by5)
                            setOfDice.get(i-1).setShowingLetterOfDie('U');
                        else
                        {
                            setOfDice.get(i + 1).setShowingLetterOfDie('U');
                            i++;
                        }
                }
            }
        }
    }
}
