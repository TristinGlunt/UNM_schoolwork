import javafx.geometry.Point2D;
import javafx.scene.control.Button;

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
public class Station extends Point2D
{

    private String stationID;
    private Track neighborTrack;        //each station has one neighbor, a track!
    private static int numOfStations = 0;
    private int leftOrRight;
    private int stationNumber;

    /**
     * Each station needs to know it's location for the GUI, the X and Y parameters,
     * and what it's station ID given by the string. -T
     * @param x x coordinate of GUI
     * @param y y coordinate of GUI
     */
    public Station(double x, double y, Track neighborTrack)
    {
        super(x, y);
        stationNumber = numOfStations;
        this.stationID = "Station" + numOfStations;     //each station will have their own unique ID, i.e "A", "1", "Chicago" -T
        this.neighborTrack = neighborTrack; //the station will need to know it's neighboring track
        numOfStations++;

    }

    public int getStationNumber() { return stationNumber; }
    public void setNeighborTrack(Track track) { neighborTrack = track; }
    public Track getNeighborTrack() { return neighborTrack; }

    @Override
    public String toString()
    {
        return stationID;
    }

    public int getLeftOrRight()
    {
        return leftOrRight;
    }

    public void setLeftOrRight(int leftOrRight)
    {
        this.leftOrRight = leftOrRight;
    }
}