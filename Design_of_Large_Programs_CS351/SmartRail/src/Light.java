import javafx.geometry.Point2D;

/**
 * =========================================
 *
 * SmartRail - CS 351 Project 3
 * @author Tristin Glunt | tglunt@unm.edu
 * @author Duong Nguyen | dnguyen@unm.edu
 * 11/16/17
 * =========================================
 */

public class Light extends Point2D
{
    private Track myTrack = null;
    private boolean locked = false;
    private boolean isGreen;
    private int goingRight; //coming from right is 1, coming from left is 0

    public Light(double x, double y)
    {
        super(x, y);
    }

    /**
     *
     * @param track set the track where the light is on it.
     *              if the track is not set then it would be NULL
     */
    public synchronized void setMyTrack(Track track)
    {
        myTrack = track;
    }
    public synchronized void setLocked(boolean expr)
    {
        locked = expr;
    }

    /**
     *  green light means the track at this location is
     *  available for any train pass through but only one at the time
     *  synchronized will lock this variable from other threads (trains)
     *  to modify it when the current thread is accessing it.
     *
     * @param isGreen set the state for this light
     *                green light means this track is available.
     *                Otherwise, it is busy. a train is reading and writing
     *                the value to it
     */
    public synchronized void setSignal(boolean isGreen, int goingRight)
    {
        this.goingRight = goingRight;
        this.isGreen = isGreen;
    }

    /**
     *  get current signal green or red.
     *
     * @return the state for this light
     *         green light means this track is available.
     *         Otherwise, it is busy. a train is reading and writing
     *         the value to it
     */
    public synchronized boolean getSignal()
    {
        return isGreen;
    }
    public synchronized boolean getLocked() { return locked; }
    public int isGoingRight() { return goingRight; }

    @Override
    public String toString()
    {
        return "Light"+hashCode();
    }
}
