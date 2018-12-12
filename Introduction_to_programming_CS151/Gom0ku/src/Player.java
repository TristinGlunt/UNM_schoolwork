/*
current player object to hold symbol of players and
if that player is on a winning streak
 */

import cs251.lab3.GomokuModel;
public class Player {
    private GomokuModel.Square symbol;
    private int winningStreak;
    private boolean currentTurn = false;

    public void setCurrentTurn(boolean val) {
        currentTurn = val;
    }

    public boolean getCurrentTurn() {
        return this.currentTurn;
    }

    public void setPlayerSymbol(GomokuModel.Square sym) {
        this.symbol = sym;
    }

    public GomokuModel.Square getPlayerSymbol() {
        return this.symbol;
    }
}
