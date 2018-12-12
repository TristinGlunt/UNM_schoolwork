import javafx.geometry.Point2D;

import java.util.List;

/**
 * =========================================
 *
 * SmartRail - CS 351 Project 3
 * @author Tristin Glunt | tglunt@unm.edu
 * @author Duong Nguyen | dnguyen@unm.edu
 * 11/16/17
 * =========================================
 */

public class Switch extends Point2D  implements Runnable
{
    public Track trackOn;  //the track the switch is on
    private Track diagonalTrack; //the diagonal track the switch has access to
    private boolean routeLock;
    private int state = 0;  //0 switch off, 1 switch on
    private int stateHolder;

    public Switch(double x, double y, Track trackOn, Track diagonalTrack)
    {
        super(x, y);
        this.trackOn = trackOn;
        this.diagonalTrack = diagonalTrack;
        Thread switchThread = new Thread(this);
        switchThread.start();
    }

    /**
     * run: Each switch is it's own thread constantly
     * making sure it's holding the right state.
     */
    @Override
    synchronized public void run()
    {
        while(true)
        {
            synchronized (this)
            {
                if(routeLock)
                {
                    state = stateHolder;
                }
                else
                {
                    try
                    {
                        this.wait();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     *
     * The below methods are to pass messages to diagonal tracks. Switches are technically
     * owned by the track, and the tracks do not have a diagonalTrack variable. Instead
     * diagonal tracks are owned by Switches, so to pass messages to a diagonal track,
     * the message is passed through the switch that is on the horizontal track
     * corresponding to the diagonal track
     *
     */

    /**
     * The switch never holds a train. If a horizonatal track is passing the train,
     * and the switch is on, it will call this method to pass it to the diag. track
     * @param train to be passed
     */
    synchronized public void passTrainToDiagonalTrack(Train train, boolean passToRight)
    {
        diagonalTrack.receiveTrain(train, passToRight);
    }

    /**         _x
     *            \      <-- Horizontal track, switch, diag track, switch, horizontal track
     *              x_       the diagonal track would call pass trainToTrackOn to pass the train
     *                       to the track the switch is actually on
     */
    synchronized public void passTrainToTrackOn(Train train, boolean passToRight)
    {
        trackOn.receiveTrain(train, passToRight);
    }

    //passes the route finding message to the diagonal track
    synchronized public List<Track> passRouteToDiagTrack(Station destination, List<Track> route, int leftOrRight)
    {
        return diagonalTrack.findRoute(destination, route, leftOrRight);
    }

    //passes the route finding message to the horizontal track from the diagonal track
    synchronized public List<Track> passRouteToTrackOn(Station destination, List<Track> route, int leftOrRight)
    {
        return trackOn.findRoute(destination, route, leftOrRight);
    }

    //passes the any possible route finding message to the diagonal track
    synchronized public List<Track> passRouteToDiagTrackAnyPoss(Station destination, List<Track> route, int leftOrRight)
    {
        return diagonalTrack.findAnyPossibleRoute(destination, route, leftOrRight);
    }

    //passes the any possible route finding message to the horizontal track from the diagonal track
    synchronized public List<Track> passRouteToTrackOnAnyPoss(Station destination, List<Track> route, int leftOrRight)
    {
        return trackOn.findAnyPossibleRoute(destination, route, leftOrRight);
    }

    @Override
    public String toString()
    {
        return "Switch"+hashCode();
    }

    synchronized public void setRouteLock(boolean cond) { routeLock = cond; stateHolder = state; }
    synchronized public void setState(int state) { this.state = state;}
    synchronized public int getState() { return state; }
    public void setDiagonalTrack(Track diagTrack)
    {
        this.diagonalTrack = diagTrack;
    }
    public Track getDiagonalTrack() { return diagonalTrack; }
}
