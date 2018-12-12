package Boggle;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 *  @author Tristin Glunt | tglunt@unm.edu | CS 351 Project 2 Boggle
 */
public class ClockDisplay
{
    private Label timeRemainingLabel;
    private Pane holdClock;

    /**
     * ClockDisplay creates our clock display with initial given time
     * @param timeRemaing initial given time to display
     */
    public ClockDisplay(int timeRemaing)
    {
        holdClock = new Pane();
        holdClock.setStyle("-fx-border-color: black");
        holdClock.setLayoutX(50);
        holdClock.setLayoutY(50);
        holdClock.setMinSize(100, 50);


        timeRemainingLabel = new Label(Integer.toString(timeRemaing));
        updateClockDisplay(timeRemaing);
        timeRemainingLabel.setStyle("-fx-font-size: 48; -fx-text-fill: red");
        timeRemainingLabel.setLayoutX(5);
        holdClock.getChildren().add(timeRemainingLabel);
                                                                               //GUI should have access to the clock display so no reason for clock
                                                                                //display to be calling methods from the GUI
    }

    /**
     * getClockDisplay
     * @return the clock display pane
     */
    public Pane getClockDisplay() { return holdClock; }

    /**
     * updateClockDisplay: updates time to be displayed
     * @param timeRemaining time passed down to be display by the clock
     */
    public void updateClockDisplay(int timeRemaining)
    {
        int minutes = (int) ((timeRemaining*1000 / (1000*60)) % 60);
        int seconds = (int) (timeRemaining*1000 / 1000) % 60 ;
        if(seconds < 10)
            timeRemainingLabel.setText(Integer.toString(minutes) + ":0" + Integer.toString(seconds));
        else
            timeRemainingLabel.setText(Integer.toString(minutes) + ":" + Integer.toString(seconds));
    }

    /**
     * setTimeRemainingLabel
     * @param val set the time remaining label to the label passed down
     */
    public void setTimeRemainingLabel(Label val) { timeRemainingLabel = val; }
}
