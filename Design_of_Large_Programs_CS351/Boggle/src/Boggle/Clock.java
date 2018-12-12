package Boggle;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Tristin Glunt | tglunt@unm.edu | CS 351 Project 2 Boggle
 */
public class Clock
{
    private static final int TOTAL_TIME = 180; //180
    private Timer timer;
    private int timeRemaing = TOTAL_TIME;
    private ClockDisplay clockDisplay;
    private Consumer stopGame;

    /**
     * start the time clock count down once the clock has been created
     * @param stopGame lambda function passed down to stop the game
     */
    public Clock(Consumer stopGame)
    {
        clockDisplay = new ClockDisplay(timeRemaing);
        this.stopGame = stopGame;
        startTime();
    }

    public Pane getClockDisplay() { return clockDisplay.getClockDisplay(); }

    /**
        Talked to Joel and he said that a separate thread for the timer is OK
        startTime: starts the count down of 3 minutes for the game, once the time
        has ran out the game gets stopped. We update the display on every iteration.
        We call the lambda function to stop the game
     */
    private void startTime()
    {
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                Platform.runLater(new Runnable()
                {
                    public void run()
                    {
                        clockDisplay.updateClockDisplay(timeRemaing);
                        timeRemaing--;
                        if (timeRemaing < 0)
                        {
                            stopTime();
                            stopGame.accept(null);
                        }
                    }
                });
            }
        };
        this.timer = new Timer();
        this.timer.schedule(task, 1000, 1000);
    }

    /**
     * stopTime: end the timer thread
     */
    private void stopTime() { timer.cancel(); }

    /**
     * resetTimer: reset the timer back to it's original start time
     */
    public void resetTimer()
    {
        timeRemaing = TOTAL_TIME;
    }

    /**
     * resetClockDisplay: reset the clock display once the game is being restarted
     */
    public void resetClockDisplay()
    {
        clockDisplay.setTimeRemainingLabel(null);
        clockDisplay = null;
    }

}
