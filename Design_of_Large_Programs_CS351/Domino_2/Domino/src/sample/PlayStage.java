package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;

/**
 * Created by tristin on 8/25/2017.
 */
public class PlayStage
{
    GridPane playStage = new GridPane();
    Pane topRowPane = new Pane();
    Pane bottomRowPane = new Pane();


    private ArrayList<BoardPiece> topPlayed = new ArrayList<>(14);
    private ArrayList<BoardPiece> bottomPlayed = new ArrayList<>(14);
    private static final int PIECES_PER_ROW = 14;
    private static final int MIDDLE_LOCATION = PIECES_PER_ROW/2;
    private BoardPiece leftEndPiece, rightEndPiece;
    private int leftCurrentIndex, rightCurrentIndex;
    boolean firstMove = true;

    /*
        The constructor for PlayStage creates two array lists
         that will act as rows for our dominos to be played on,
         it also fills this array list up with BoardPieces that
         are labeled which row they are in and initialized with 0 values
     */
    public PlayStage()
    {
        for(int i = 0; i < 14; i++)
        {
            topPlayed.add(i,new BoardPiece(1, 0, 0));
            bottomPlayed.add(i, new BoardPiece(0, 0, 0));
        }
    }

    public BoardPiece getLeftEndPiece() { return leftEndPiece; }
    public BoardPiece getRightEndPiece() { return rightEndPiece; }

    public boolean playPieceOnStage(BoardPiece pieceSelected, String rightOrLeft)
    {
        if(firstMove)
        {
            pieceSelected.setPlayedOnStage(true);
            bottomPlayed.set(MIDDLE_LOCATION, pieceSelected);
            leftEndPiece = pieceSelected;
            rightEndPiece = pieceSelected;
            leftCurrentIndex = MIDDLE_LOCATION;
            rightCurrentIndex = MIDDLE_LOCATION;
            rightEndPiece.topOrBottomOfStage = 0;
            leftEndPiece.topOrBottomOfStage = 0;
            firstMove = false;

        } else if(rightOrLeft.equals("Right"))
        {
            if(pieceSelected.topValue == rightEndPiece.bottomValue)
            {

                if(pieceSelected.topOrBottomOfStage == 1)
                {
                    System.out.println("in top loop");

                    topPlayed.set(rightCurrentIndex, pieceSelected);
                    rightEndPiece = pieceSelected;
                    //rightCurrentIndex++;

                } else if(pieceSelected.topOrBottomOfStage == 0)
                {
                    bottomPlayed.set(rightCurrentIndex+1, pieceSelected);
                    rightEndPiece = pieceSelected;
                    rightCurrentIndex++;

                }
                pieceSelected.setPlayedOnStage(true);
                return true;

            } else if(pieceSelected.bottomValue == rightEndPiece.bottomValue)
            {

                if(pieceSelected.topOrBottomOfStage == 1)
                {
                    System.out.println("in top loop");
                    pieceSelected.setValues(pieceSelected.bottomValue, pieceSelected.topValue);
                    topPlayed.set(rightCurrentIndex, pieceSelected);
                    rightEndPiece = pieceSelected;
                    //rightCurrentIndex++;

                } else if(pieceSelected.topOrBottomOfStage == 0)
                {
                    System.out.println("in bottom right loop");
                    pieceSelected.setValues(pieceSelected.bottomValue, pieceSelected.topValue);  //switch values around because the bottom # was the one that matched
                    bottomPlayed.set(rightCurrentIndex+1, pieceSelected);
                    rightEndPiece = pieceSelected;
                    rightCurrentIndex++;

                }
                pieceSelected.setPlayedOnStage(true);
                return true;
            }
            return false;

        } else if(rightOrLeft.equals("Left"))
        {
            if(pieceSelected.topValue == leftEndPiece.topValue)
            {

                if(pieceSelected.topOrBottomOfStage == 1)
                {
                    System.out.println("in top left loop");
                    pieceSelected.setValues(pieceSelected.bottomValue, pieceSelected.topValue); //switch vals to match stage val
                    topPlayed.set(leftCurrentIndex-1, pieceSelected);
                    leftEndPiece = pieceSelected;
                    leftCurrentIndex--;

                } else if(pieceSelected.topOrBottomOfStage == 0)
                {
                    pieceSelected.setValues(pieceSelected.bottomValue, pieceSelected.topValue); //switch vals to match stage val
                    bottomPlayed.set(leftCurrentIndex, pieceSelected);
                    leftEndPiece = pieceSelected;
                    //leftCurrentIndex--;

                }
                pieceSelected.setPlayedOnStage(true);
                return true;

            } else if(pieceSelected.bottomValue == leftEndPiece.topValue)
            {

                if(pieceSelected.topOrBottomOfStage == 1)
                {
                    System.out.println("in bottom left loop");
                    topPlayed.set(leftCurrentIndex-1, pieceSelected);
                    leftEndPiece = pieceSelected;
                    leftCurrentIndex--;

                } else if(pieceSelected.topOrBottomOfStage == 0)
                {
                    bottomPlayed.set(leftCurrentIndex, pieceSelected);
                    leftEndPiece = pieceSelected;
                    //leftCurrentIndex--;

                }
                pieceSelected.setPlayedOnStage(true);
                return true;

            }
            return false;
        }
        return false;
    }

    public void printPlayStage()
    {
        System.out.print("  ");
        for(BoardPiece step : topPlayed)
        {
            System.out.print(step.topValue + " " + step.bottomValue + "  ");
        }

        System.out.println(" ");

        for(BoardPiece step : bottomPlayed)
        {
            System.out.print(step.topValue + " " + step.bottomValue + "  ");
        }
    }

    public void displayPlayStage()
    {
        playStage.setAlignment(Pos.CENTER);
        playStage.setHgap(1);
        playStage.setVgap(3);
        //playStage.setPrefSize(100, 10);
        //playStage.setGridLinesVisible(true);
        playStage.setPadding(new Insets(0, 0, 0, 50));

        RowConstraints row1 = new RowConstraints(15);
        RowConstraints row2 = new RowConstraints(21);
        RowConstraints row3 = new RowConstraints(21);
        playStage.getRowConstraints().addAll(row1, row2, row3);



        //topRowPane.setStyle("-fx-border-color: black");
        //bottomRowPane.setStyle("-fx-border-color: black");

        for(int i = 0; i < 14; i++)
        {
            topPlayed.get(i).setMinWidth(50);
            bottomPlayed.get(i).setMinWidth(50);
            //topPlayed.get(i).setMaxSize(2, 2);
            topPlayed.get(i).getStylesheets().add(getClass().getResource("PlayStage.css").toExternalForm());
            bottomPlayed.get(i).getStylesheets().add(getClass().getResource("PlayStage.css").toExternalForm());
        }


        int columnCounter = 205;
        int rowCounter = 1;
        for(int j = 0; j < 14; j++)
        {
            topPlayed.get(j).setLayoutX(columnCounter);
            topPlayed.get(j).setLayoutY(rowCounter);
            topRowPane.getChildren().add(topPlayed.get(j));
            columnCounter += 50;
        }

        columnCounter = 181;
        rowCounter = 1;
        for(int j = 0; j < 14; j++)
        {
            bottomPlayed.get(j).setLayoutX(columnCounter);
            bottomPlayed.get(j).setLayoutY(rowCounter);
            bottomRowPane.getChildren().add(bottomPlayed.get(j));
            columnCounter += 50;
        }
        playStage.addRow(1, topRowPane);
        playStage.addRow(2, bottomRowPane);
    }
}
