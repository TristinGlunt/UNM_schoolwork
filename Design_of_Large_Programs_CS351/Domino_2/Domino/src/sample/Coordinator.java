package sample;

import java.util.ArrayList;

/**
 * The Coordinator will simply be the rule keeper of the game
 */
public class Coordinator
{
    private static PlayStage playStage = new PlayStage();
    private static Boneyard boneyard = new Boneyard(28);
    private Player player1 = new Player(0, playStage, boneyard);
    private Player player2 = new Player(0, playStage, boneyard);

    private static final int PIECES_START_OF_GAME = 7;

    public Coordinator()
    {
        boolean gameOver = false;
        player1.setMyTurn(true);

        boneyard.buildBoneyard();
        setupGame();
        printPlayerHands(); //DEBUGGING

        while(!gameOver)
        {
            System.out.println("in game over");
            if(player1.getMyTurn())
            {
                System.out.println("in player1 turn");
                BoardPiece selectedPiece = player1.getHand().playPiece();

                if(playStage.playPieceOnStage(selectedPiece, player1.getHand().whichSideToPlayOn()))
                    player1.getHand().getPlayerHand().remove(selectedPiece);
                playStage.printPlayStage();
                player1.setMyTurn(false);
                player2.setMyTurn(true);
            } else if(player2.getMyTurn())
            {
                BoardPiece selectedPiece = player2.getHand().playPiece();
                if(playStage.playPieceOnStage(selectedPiece, player2.getHand().whichSideToPlayOn()))
                    player2.getHand().getPlayerHand().remove(selectedPiece);
                playStage.printPlayStage();
                player1.setMyTurn(true);
                player2.setMyTurn(false);
            }

            gameOver = checkGameOver(player1.getHand().getPlayerHand(), player2.getHand().getPlayerHand());
        }
        System.out.println("Game is over! :D");
    }

    /*
        SetupGame is a simple function that gives both of the players 7 dominos each
        from the boneyard
     */
    private void setupGame()
    {
        while(player1.getHand().getAmountOfPiecesPicked() < PIECES_START_OF_GAME)
        {
            boneyard.giveBoardPiece(player1);
        }

        while(player2.getHand().getAmountOfPiecesPicked() < PIECES_START_OF_GAME)
        {
            boneyard.giveBoardPiece(player2);
        }
    }

    private boolean checkGameOver(ArrayList<BoardPiece> player1Hand, ArrayList<BoardPiece> player2Hand)
    {
        boolean player1OutOfDominos = true;
        boolean player2OutOfDominos = true;

        for(BoardPiece step1 : player1Hand)
        {
            if(step1.getPickedFromBoneyard()) { player1OutOfDominos = false; break;}
        }

        for(BoardPiece step2 : player2Hand)
        {
            if(step2.getPickedFromBoneyard()) { player2OutOfDominos = false; break;}
        }

        if(player1OutOfDominos) { System.out.println("\nplayer1 wins!"); return true; }
        else if(player2OutOfDominos) { System.out.println("\nplayer2 wins!"); return true; }

        return false;
    }

    private void printPlayerHands()
    {
        for(BoardPiece step : player1.getHand().getPlayerHand())
        {
            System.out.println(step.topValue + " " + step.bottomValue);
        }

        for(BoardPiece step : player2.getHand().getPlayerHand())
        {
            System.out.println(step.topValue + " " + step.bottomValue);
        }
    }
}
