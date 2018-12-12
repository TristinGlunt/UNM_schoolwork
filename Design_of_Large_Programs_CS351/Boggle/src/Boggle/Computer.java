package Boggle;

import java.util.HashSet;

/**
 * Finds all possible solutions in the board
 */
public class Computer
{
    private HashSet<String> dictionary;
    private Dice[][] gameBoard;
    private int boardDim;
    private HashSet<String> wordsInBoard = new HashSet<>();

    /**
     *
     * @param gameBoard set the gameboard passed down from the Coordinator after the gameboard is made
     * @param boardDim the size of the board
     */
    public Computer(Dice[][] gameBoard, int boardDim)
    {
        this.boardDim = boardDim;
        createDictionary();
        this.gameBoard = gameBoard;
        findAllPossibleWords();
    }

    /**
     * @return words in the board
     */
    public HashSet<String> getWordsInBoard() { return wordsInBoard; }

    /**
     * createDictionary:
     * Call dictionary to create the dictionary
     */
    private void createDictionary()
    {
        Dictionary dictionaryObject = new Dictionary();
        dictionary = dictionaryObject.getDictionary();
    }

    /**
     * findAllPossibleWords:
     * loops through every individual index of the board and calls checkPossilbeWords
     * to find all solutions
     */
    private void findAllPossibleWords()
    {
        for(int i = 0; i < boardDim; i++)
        {
            for(int j = 0;j < boardDim; j++)
            {
                checkPossibleWords(Character.toString(gameBoard[i][j].getShowingLetterOfDie()).toLowerCase(), i, j, dictionary);
            }
        }
    }

    /**
     * checkPossibleWords:
     * starting from a given row and col, builds all possible word combinations that are contained
     * in our given dictionary. Recursive function that keeps calling itself until either all indices
     * have been checked our our currentDict is empty
     * @param currentStringSubset the word we are currently building
     * @param row row given of current dice we're checking
     * @param col col given of current dice we're checking
     * @param currentDict our current dictionary that contains subsets of our currentStringSubset except for the first iteration
     */
    private void checkPossibleWords(String currentStringSubset, int row, int col, HashSet<String> currentDict)
    {
        HashSet<String> tempDict = new HashSet<>();
        if(gameBoard[row][col].getClicked())
        {
            return;
        }

        for(String step : currentDict)
        {
            if(step.startsWith(currentStringSubset))
            {
                tempDict.add(step); //subset of dictionary with all words starting with starting char
            }
        }

        if(tempDict.isEmpty())
        {
            //if the entire current dictionary doesn't contain any words that start with our current
            //subset, no reason to continue looking down this recursion
            return;
        }

        if(currentDict.contains(currentStringSubset))
        {
            wordsInBoard.add(currentStringSubset);
        }

        gameBoard[row][col].setClicked(true);

        if(row > 0)
        {
            String tempSubSet = currentStringSubset + Character.toString(gameBoard[row-1][col].getShowingLetterOfDie()).toLowerCase();
            checkPossibleWords(tempSubSet, row - 1, col, tempDict);
            if(col > 0)
            {
                tempSubSet = currentStringSubset + Character.toString(gameBoard[row-1][col-1].getShowingLetterOfDie()).toLowerCase();
                checkPossibleWords(tempSubSet, row-1, col-1, tempDict);
            }
            if(col < (boardDim - 1))
            {
                tempSubSet = currentStringSubset + Character.toString(gameBoard[row-1][col+1].getShowingLetterOfDie()).toLowerCase();
                checkPossibleWords(tempSubSet, row-1, col+1, tempDict);
            }
        }
        if(col > 0)
        {
            String tempSubSet = currentStringSubset + Character.toString(gameBoard[row][col-1].getShowingLetterOfDie()).toLowerCase();
            checkPossibleWords(tempSubSet, row, col-1, tempDict);
        }
        if(col < (boardDim - 1))
        {
            String tempSubSet = currentStringSubset + Character.toString(gameBoard[row][col+1].getShowingLetterOfDie()).toLowerCase();
            checkPossibleWords(tempSubSet, row, col+1, tempDict);
        }
        if(row < (boardDim - 1))
        {
            if(col > 0)
            {
                String tempSubSet = currentStringSubset + Character.toString(gameBoard[row+1][col-1].getShowingLetterOfDie()).toLowerCase();
                checkPossibleWords(tempSubSet, row+1, col-1, tempDict);
            }
            if(col < (boardDim - 1))
            {
                String tempSubSet = currentStringSubset + Character.toString(gameBoard[row+1][col+1].getShowingLetterOfDie()).toLowerCase();
                checkPossibleWords(tempSubSet, row+1, col+1, tempDict);
            }
            String tempSubSet = currentStringSubset + Character.toString(gameBoard[row+1][col].getShowingLetterOfDie()).toLowerCase();
            checkPossibleWords(tempSubSet, row+1, col, tempDict);
        }

        gameBoard[row][col].setClicked(false);
    }

    /**
     * resetGameBoardToNotClicked:
     * goes through the entire gameboard to not be clicked
     */
    private void resetGameBoardToNotClicked()
    {
        for(int i = 0; i < boardDim; i++)
        {
            for(int j = 0;j < boardDim; j++)
            {
                gameBoard[i][j].setClicked(false);
            }
        }

    }

    /**
     * resetComputer: reset the computer to null so no solutions have been found,
     * called whenever game is being restarted
     */
    public void resetComputer()
    {
        resetGameBoardToNotClicked();
        gameBoard = null;
        wordsInBoard = null;
    }
}
