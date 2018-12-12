package Boggle;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.IntConsumer;


/**
 * The Coordinator creates many of the different objects of the Boggle program.
 * It handles most of our actual logic of the game such as checking if a guessed
 * word is valid
 * @author Tristin Glunt | tglunt@unm.edu | CS 351 Project 2 Boggle
 */
public class Coordinator
{
    private Clock clock;
    private GameBoard gameBoard;
    private Computer computer;

    private HashSet<String> wordsInBoard;
    private HashSet<String> untouchedWordsInBoard;
    private ArrayList<String> correctlyGuessedWords;
    private ArrayList<String> incorrectlyGuessedWords;
    private String currentGuessedWord = "";
    private Stage primaryStage;
    private Scene startScene;
    private IntConsumer gameOverDisplay;

    /**
     *
     * @param startScene start scene passed down from the GUI so when the game is restarted the start scene is displayed
     * @param boardDim board dimension passed down from whichever button is clicked at start menu of GUI
     * @param primaryStage used to set the scene of the GUI
     * @param gameOverDisplay used to set the display once the timer has run out and game stops
     */
    public Coordinator(Scene startScene, int boardDim, Stage primaryStage, IntConsumer gameOverDisplay)
    {
        this.startScene = startScene;
        this.gameOverDisplay = gameOverDisplay;

        this.primaryStage = primaryStage;
        correctlyGuessedWords = new ArrayList<>();
        incorrectlyGuessedWords = new ArrayList<>();

        clock = new Clock(j -> stopGame()); //lambda expression to pass down stop game function
                                            //this is a beautiful "hack" imo so the clock doesn't get the entire
                                            //coord object
        giveBoardDimensionsAndStart(boardDim);
    }

    public ArrayList<String> getIncorrectlyGuessedWords() { return incorrectlyGuessedWords; }
    public ArrayList<String> getCorrectlyGuessedWords() { return correctlyGuessedWords; }
    public GridPane getGameBoardDisplay() { return gameBoard.getGameBoardDisplay(); }
    public Button getRotateRightButton() { return gameBoard.getRotateRightButton(); }
    public Button getRotateLeftButton() { return gameBoard.getRotateLeftButton(); }
    public Pane getClockDisplay() { return clock.getClockDisplay(); }

    /**
     * giveBoardDimensionsAndStart: creates the gameboard with the correct
     * board dimensions and finds the solutions of the created gameboard
     * @param boardDim passes down the board dimensions f the game
     */
    private void giveBoardDimensionsAndStart(int boardDim)
    {
        gameBoard = new GameBoard(boardDim, j -> addLetterToCurrentWord(j));
        computer = new Computer(gameBoard.getGameboard(), boardDim);
        wordsInBoard = computer.getWordsInBoard();
        untouchedWordsInBoard = computer.getWordsInBoard();
    }

    /**
     * addLetterToCurrentWord: takes the current letter being inputted and adds it
     * to the word the user is building
     * @param letter gets added to the string we build for the users guess
     */
    private void addLetterToCurrentWord(char letter) { currentGuessedWord = currentGuessedWord + letter; }

    /**
     * resetGuessedWord: take the current guessed word and set it to empty
     */
    public void resetGuessedWord() { currentGuessedWord = ""; }

    /**
     * enterGuessedWord: From the enterListener we'll pass the guessed word to
     * the coordinator to check if it's in the solution dictionary from the computer
     */
    public void enterGuessedWord() { guessedWord(currentGuessedWord.toLowerCase()); }

    /**
     * guessedWord: check the entered word if it in our found solutions
     * @param guessedWord the entered word to check if valid
     */
    private void guessedWord(String guessedWord)
    {
        if(wordsInBoard.contains(guessedWord))
        {
            correctlyGuessedWords.add(guessedWord);
            wordsInBoard.remove(guessedWord);
        }
        else
        {
            if(!guessedWord.equals(""))
                incorrectlyGuessedWords.add(guessedWord);
        }
    }

    /**
     * stopGame: stop the game once the timer has ran out and calculate
     * the total points from correctly guessed word list
     */
    private void stopGame()
    {
        gameOverDisplay.accept(getTotalPoints());
    }

    /**
     * possibleWords: builds a string of the solutions the solutions object
     * found (all the valid words in the board)
     * @return the string of all of the words
     */
    public String possibleWords()
    {
        String listOfWords = "";
        int counter = 0;
        for(String word : untouchedWordsInBoard)
        {
            if(counter == 13)
            {
                listOfWords = listOfWords + word + "\n";
                counter = 0;
                continue;
            }
            listOfWords = listOfWords + word + " ";
            counter++;
        }
        return listOfWords;
    }

    /**
     * getTotalPoints: calculate the total points from the correctlyGuessedWords object
     * @return int of the correctly guessed words calculated based on points from the rules
     */
    public int getTotalPoints()
    {
        int totalPoints = 0;
        for(String step : correctlyGuessedWords)
        {
            totalPoints += step.length()-2;
        }
        return totalPoints;
    }

    /**
     * resetGameBoardAfterEnter: once enter has been pressed to enter a built word,
     * reset the highlighting and others from the gameboard
     */
    public void resetGameBoardAfterEnter()
    {
        gameBoard.resetGameBoardAfterEnter();
    }

    /**
     * restartGame: when play again has been pressed from the GUI, reset all of our objects
     * and re-launch the start scene
     */
    public void restartGame()
    {
        clock.resetTimer();
        computer.resetComputer();
        computer = null;
        correctlyGuessedWords = null;
        incorrectlyGuessedWords = null;
        gameBoard = null;
        wordsInBoard = null;

        clock.resetClockDisplay();

        primaryStage.setScene(startScene);
    }
}