package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * We create a new player.
 * This player has their own hand.
 * This player keeps track of their initial dominos drawn from boneyard => PlayerHand
 * This player keeps track of if it's their turn or not.
 * This player keeps track if they have any moves or not
 * This player has a method to handle events from clicking on their hand
 */
public class Player
{
    private PlayerHand hand;
    private static PlayStage playStage;
    private static Boneyard boneyard;
    private int typeOfPlayer; //0 for human 1 for computer
    private boolean myTurn;
    private boolean moveIsAvailable;

    public Player(int typeOfPlayer, PlayStage playStage, Boneyard boneyard)
    {
        hand = new PlayerHand();
        this.typeOfPlayer = typeOfPlayer;
        this.playStage = playStage;
        this.boneyard = boneyard;
        //dominoHand = hand.handArray;
    }

    public void setTypeOfPlayer(int typeOfPlayer) { this.typeOfPlayer = typeOfPlayer; }
    public int getTypeOfPlayer() { return this.typeOfPlayer; }

    public void setMyTurn(boolean turn) { myTurn = turn; }
    public boolean getMyTurn() { return myTurn; }

    public PlayerHand getHand() { return hand; }
    //may add getHandDisplayUpdate()!

    public boolean isMoveAvailable()
    {
        //loop through hand and compare to playStage
        return true;
    }

}
