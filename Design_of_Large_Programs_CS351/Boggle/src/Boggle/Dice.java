package Boggle;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Dice: our dice our buttons for our Boggle project, each
 * die has a 6 different characters, and it's showing letter die is the
 * side of the die that's facing up.
 *
 * I implemented it this way so there would be much more words in the game
 * like how Boggle is actually played.
 */
public class Dice extends Button
{
    private ArrayList<Character> die = new ArrayList<>(6);
    private char showingLetterOfDie;
    private boolean clicked = false;
    private int row;
    private int col;

    /**
     * Dice: the characters of the die
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     */
    public Dice(char a, char b, char c, char d, char e, char f)
    {
        die.add(0, a);
        die.add(1, b);
        die.add(2, c);
        die.add(3, d);
        die.add(4, e);
        die.add(5, f);
        showingLetterOfDie = a;
        setDieDisplay();
    }

    /**
     * getShowingLetterOfDie:
     * @return showing letter of die
     */
    public char getShowingLetterOfDie() { return showingLetterOfDie; }
    public void setShowingLetterOfDie(char showingLetterOfDie)
    {
        this.showingLetterOfDie = showingLetterOfDie;
        setDieDisplay();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    /**
     * setRowAndCol: set the row and column the die is in in the
     * gameboard
     * @param i the row of the die
     * @param j the column of the die
     */
    public void setRowAndCol(int i, int j)
    {
        row = i;
        col = j;
    }

    /**
     * getClicked:
     * if the die has been clicked on the GUI, this method is marked true
     * @return whether or not the die has been clicked
     */
    public boolean getClicked() { return clicked; }

    /**
     * setClicked: set whether or not the die has been clicked
     * @param expr the boolean expr if the die has been clicked or not
     */
    public void setClicked(boolean expr) { clicked = expr; }

    /**
     * rollDie:
     * shuffles the values of the die and sets it's showingLetter to be index 0
     */
    public void rollDie()
    {
        Collections.shuffle(die);
        showingLetterOfDie = die.get(0);
        setDieDisplay();
    }


    /**
     * setDieDisplay:
     * for the GUI, set how the Die looks
     */
    private void setDieDisplay()
    {
        this.setMinSize(100, 100);
        this.setStyle("-fx-font-size: 32; -fx-background-color: rgb(179, 179, 179); -fx-border-color: black");
        this.setText(Character.toString(showingLetterOfDie));
    }

}
