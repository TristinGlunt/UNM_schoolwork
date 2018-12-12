package sample;


import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.shape.Rectangle;

public class BoardPiece extends Button
{
    public int topValue, bottomValue;
    public int topOrBottomOfStage; //0 = bottom array, 1 = topArray (for PlayStage pieces only)
    private boolean pickedFromBoneyard;
    private boolean playedOnStage;

    public BoardPiece(int topVal, int bottomVal)
    {
        setText(topVal + "\n" + bottomVal);
        topValue = topVal;
        bottomValue = bottomVal;
    }

    public BoardPiece(int rowIndic, int topVal, int bottomVal)
    {
        /*
        if(topVal == 0) { setText("        |      " + bottomVal); }
        if(bottomVal == 0) { setText(topVal + "       |          ");  }
        if(bottomVal == 0 && topVal == 0) { setText("        |          "); }
        if(bottomVal != 0 && topVal != 0) { setText(topVal + "      |      " + bottomVal); }
        */
        topValue = topVal;
        bottomValue = bottomVal;
        topOrBottomOfStage = rowIndic; //top = 1, bottom  = 0
    }

    public void setValues(int topVal, int bottomVal)
    {
        topValue = topVal;
        bottomValue = bottomVal;
        setText(topVal + "\n" + bottomVal);
    }

    public void setStageValues(int topVal, int bottomVal)
    {
        if(topVal == 0) { setText("        |      " + bottomVal); }
        if(bottomVal == 0) { setText(topVal + "       |          ");  }
        if(bottomVal == 0 && topVal == 0) { setText("        |          "); }
        if(bottomVal != 0 && topVal != 0) { setText(topVal + "      |      " + bottomVal); }
        topValue = topVal;
        bottomValue = bottomVal;
    }

    public boolean getPickedFromBoneyard() { return pickedFromBoneyard; }
    public void setPickedFromBoneyard(boolean val) { pickedFromBoneyard = val; }

    public boolean getPlayedOnStage() { return playedOnStage; }
    public void setPlayedOnStage(boolean val) { playedOnStage = val; }

    public int getTopOrBottom() { return topOrBottomOfStage; }
    public void setTopOrBottom(int val) { topOrBottomOfStage = val; }
}
