package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Boneyard will include all of the Boneyard functionality
 * for the game Dominoes, as well has the functionality
 * for the beginning of the game where players
 * are selecting their first 7 Dominoes
 */
public class Boneyard
{
    private GridPane boneyardDisplay = new GridPane(); //boneyard gridPane

    private static ArrayList<BoardPiece> boardPieces; //individual pieces and can be considered as the boneyard structure
    private int amountOfPieces;
    private int positionOfTop = 0;

    public Boneyard(int amountOfPieces)
    {
        this.amountOfPieces = amountOfPieces;
        boardPieces = new ArrayList<>(amountOfPieces);
    }

    public void buildBoneyard() //build boneyard builds the domino set and the boneyard structure (an array)
    {
        int topValCounter = 0;
        int bottomValCounter = 0;
        for(int z = 0; z < amountOfPieces; z++)
        {
            boardPieces.add(z, new BoardPiece(topValCounter, bottomValCounter));
            if(bottomValCounter == topValCounter)
            {
                topValCounter = 0;
                bottomValCounter++;
            } else { topValCounter++; }
        }

        Collections.shuffle(boardPieces); //shuffle the boneyard after building all of the pieces
        //printBoneYard(); DEBUGGING
    }

    public int getAmountOfPieces() { return amountOfPieces; }
    public void setAmountOfPieces(int amountOfPieces) { this.amountOfPieces = amountOfPieces; }

    public void giveBoardPiece(Player player)
    {
        if (!(positionOfTop >= amountOfPieces))
        {
            BoardPiece topOfDeck = boardPieces.get(positionOfTop);
            //System.out.println("Top of deck: " + topOfDeck.topValue + " " + topOfDeck.bottomValue); DEBUGGING
            player.getHand().addPieceToHand(topOfDeck);               //give the domino at the top of the deck to this players hand
            boardPieces.remove(topOfDeck);                            //remove the domino given
            topOfDeck = null;
            positionOfTop++;                                          //make sure the top position has been updated
        } else { System.err.println("THE BONEYARD IS OUT OF PIECES"); }

        //TODO maybe updateDisplay() method here of Boneyard after a piece has been given
    }

    public void printBoneYard()
    {
        for(BoardPiece step : boardPieces)
        {
            System.out.println(step.topValue + " " + step.bottomValue);
        }
    }

    public void buildBoneyardDisplay()
    {
        boneyardDisplay.setAlignment(Pos.CENTER_RIGHT);
        boneyardDisplay.setHgap(5);
        boneyardDisplay.setVgap(3);
        boneyardDisplay.setPadding(new Insets(12, 5, 5, 1));
        //DEBUGGING
        //boneyard.setGridLinesVisible(true);

        /*
            Create Domino's with top and bottom values;
            also add each Domino to the ArrayList bagOfPieces
            and give each domino their personal .css visuals
         */

        //add css file to all of the boneyard pieces
        //TODO update boneyard display to just be a tile
        int topValCounter = 0;
        int bottomValCounter = 0;
        for(int z = 0; z < amountOfPieces; z++)
        {
            boardPieces.get(z).getStylesheets().add(getClass().getResource("button.css").toExternalForm());
        }


        //TODO this is the placement of the pieces, can convert this to tile later
        int columnCounter = 10;
        int rowCounter = 0;
        for (int i = 0; i < amountOfPieces; i++) //build the graveyard in random order on each new game
        {
            boneyardDisplay.add(boardPieces.get(i), columnCounter, rowCounter);
            rowCounter++;
            if (rowCounter > 6)
            {
                columnCounter++;
                rowCounter = 0;
            }
        }
    }
}

       /*
        BoardPiece piece1 = new BoardPiece(0, 0);
        BoardPiece piece2 = new BoardPiece(0, 1);
        BoardPiece piece3 = new BoardPiece(1, 1);
        BoardPiece piece4 = new BoardPiece(0, 2);
        BoardPiece piece5 = new BoardPiece(1, 2);
        BoardPiece piece6 = new BoardPiece(2, 2);
        BoardPiece piece7 = new BoardPiece(0, 3);
        BoardPiece piece8 = new BoardPiece(1, 3);
        BoardPiece piece9 = new BoardPiece(2, 3);
        BoardPiece piece10 = new BoardPiece(3, 3);
        BoardPiece piece11 = new BoardPiece(0, 4);
        BoardPiece piece12 = new BoardPiece(1, 4);
        BoardPiece piece13 = new BoardPiece(2, 4);
        BoardPiece piece14 = new BoardPiece(3, 4);
        BoardPiece piece15 = new BoardPiece(4, 4);
        BoardPiece piece16 = new BoardPiece(0, 5);
        BoardPiece piece17 = new BoardPiece(1, 5);
        BoardPiece piece18 = new BoardPiece(2, 5);
        BoardPiece piece19 = new BoardPiece(3, 5);
        BoardPiece piece20 = new BoardPiece(4, 5);
        BoardPiece piece21 = new BoardPiece(5, 5);
        BoardPiece piece22 = new BoardPiece(0, 6);
        BoardPiece piece23 = new BoardPiece(1, 6);
        BoardPiece piece24 = new BoardPiece(2, 6);
        BoardPiece piece25 = new BoardPiece(3, 6);
        BoardPiece piece26 = new BoardPiece(4, 6);
        BoardPiece piece27 = new BoardPiece(5, 6);
        BoardPiece piece28 = new BoardPiece(6, 6);


        bagOfPieces.add(piece1);
        bagOfPieces.add(piece2);
        bagOfPieces.add(piece3);
        bagOfPieces.add(piece4);
        bagOfPieces.add(piece5);
        bagOfPieces.add(piece6);
        bagOfPieces.add(piece7);
        bagOfPieces.add(piece8);
        bagOfPieces.add(piece9);
        bagOfPieces.add(piece10);
        bagOfPieces.add(piece11);
        bagOfPieces.add(piece12);
        bagOfPieces.add(piece13);
        bagOfPieces.add(piece14);
        bagOfPieces.add(piece15);
        bagOfPieces.add(piece16);
        bagOfPieces.add(piece17);
        bagOfPieces.add(piece18);
        bagOfPieces.add(piece19);
        bagOfPieces.add(piece20);
        bagOfPieces.add(piece21);
        bagOfPieces.add(piece22);
        bagOfPieces.add(piece23);
        bagOfPieces.add(piece24);
        bagOfPieces.add(piece25);
        bagOfPieces.add(piece26);
        bagOfPieces.add(piece27);
        bagOfPieces.add(piece28);
        */

