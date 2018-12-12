package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.valueOf;

/**
 * Created by tristin on 8/25/2017.
 */
public class PlayerHand
{
    private final int SIZE_OF_HAND = 14;
    private GridPane player1HandDisplay = new GridPane(); //player1 (bottom player) girdPane

    private ArrayList<BoardPiece> playerHand = new ArrayList<>(14);
    private int amountOfPiecesPicked;
    private BoardPiece selectedPiece = null;

    public PlayerHand()
    {

        //create "empty" player hands
        for (int i = 0; i < SIZE_OF_HAND; i++)
        {
            BoardPiece newBoardPiece = new BoardPiece(0, 0);
            newBoardPiece.setPickedFromBoneyard(false);                     //initial values given not official picked
            playerHand.add(i, newBoardPiece);
        }
    }

    public ArrayList<BoardPiece> getPlayerHand() { return playerHand; }

    public int getAmountOfPiecesPicked() { return amountOfPiecesPicked; }

    public void addPieceToHand(BoardPiece drawnBoardPiece)
    {
        drawnBoardPiece.setPickedFromBoneyard(true);            //this piece has been drawn from the bone yard
        playerHand.set(amountOfPiecesPicked, drawnBoardPiece);
        amountOfPiecesPicked++;
    }

    /*
        PlayPiece will give the PlayStage the BoardPiece the player selects from their hand
        the COORDINATOR will set the selected piece back = to null once the turn is over.
     */
    public BoardPiece playPiece()
    {
        //this while won't get passed until a piece is clicked on
        //while(selectedPiece == null) { }
        //all temp code till gui is implemented
        System.out.println("What piece would you like to play?");
        for(BoardPiece step : playerHand)
        {
            System.out.print(step.topValue + " " + step.bottomValue + "  ");
        }
        Scanner sc = new Scanner(System.in);
        String boardPieceNums = sc.nextLine();
        StringBuilder boardPieceNums2 = new StringBuilder(boardPieceNums);

        System.out.println("What row would you like to play in?");
        Scanner sc2 = new Scanner(System.in);
        String boardPieceRow = sc2.nextLine();
        int ro = Character.getNumericValue(boardPieceRow.charAt(0));
        System.out.println(ro);



        int num1 = Character.getNumericValue(boardPieceNums2.charAt(0));

        selectedPiece = playerHand.get(num1);
        selectedPiece.topOrBottomOfStage = ro;

        playerHand.remove(num1);

        return selectedPiece;
    }

    public String whichSideToPlayOn()
    {
        System.out.println("Which side would you like to play on?");
        Scanner sc = new Scanner(System.in);
        String sideToPlayOn = sc.nextLine();
        return sideToPlayOn;
    }


    /*
        SetSelectedPiece will be called be the handle that these pieces have once the intial
        setup of the game is over
     */
    public void setSelectedPiece(ActionEvent event)
    {
        selectedPiece = (BoardPiece) event.getSource();
    }

    public boolean movesAvailable(BoardPiece leftEndPiece, BoardPiece rightEndPiece)
    {
        return true;
    }


    public void displayPlayerHand()
    {
        player1HandDisplay.setAlignment(Pos.BOTTOM_CENTER);
        player1HandDisplay.setHgap(5);
        player1HandDisplay.setVgap(3);
        player1HandDisplay.setPadding(new Insets(12, 5, 10, 5));
        //DEBUGGING
        player1HandDisplay.setGridLinesVisible(true);

        int handColumn = 1;
        int handRow = 1;
        for (int i = 0; i < 14; i++)
        {
            playerHand.get(i).getStylesheets().add(getClass().getResource("PlayerHand.css").toExternalForm());
            player1HandDisplay.add(playerHand.get(i), handColumn, handRow);
            handColumn++;
        }
    }
}
