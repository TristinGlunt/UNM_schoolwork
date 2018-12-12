import javafx.geometry.Point2D;
import java.util.ArrayList;
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

public class Train extends Point2D implements Runnable
{
    private Track trackCurrentlyOn;
    private List<Track> route;
    private Object syncObject;
    private String trainId = "";
    private Station stationDockedAt;
    private Station stationDestination;

    private boolean waitAtLight = false;
    private boolean animationFinished = true;
    private boolean arrivedAtLight = false;
    private boolean routeGood = false;

    private double tempX, tempY, stepX, stepY, guiX, guiY;
    private static int numOfTrains = 0;


    public Train(double x, double y, Track trackCurrentlyOn, List<Track> route, Object syncObject, Station stationDockedAt)
    {
        super(x, y);    //set x and y coordinates
        this.trackCurrentlyOn = trackCurrentlyOn;
        this.stationDockedAt = stationDockedAt;
        this.route = route;
        this.syncObject = syncObject;
        trainId = Integer.toString(numOfTrains);
        numOfTrains++;
        Thread train = new Thread(this);
        train.start();  //start thread
    }

    /**
     * run: each Train object is a thread that
     * continously runs this method. The first thing each train does is wait
     * until it is given a route to go to. The train then determines if it's supposed to
     * wait at a light. The train also determines whether it has reached it's destination.
     */
    @Override
    public void run()
    {
        while(true)
        {
            synchronized (syncObject)
            {
                try
                {
                    syncObject.wait();  //wait until notified by AnimationTimer in main, Track is also synced with this
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (route != null)
                {
                    if(waitAtLight && (trackCurrentlyOn.getLight() != null) && arrivedAtLight)
                    {
                        trackCurrentlyOn.setLocked(false);
                        trackCurrentlyOn.resetVisitedTracks();

                        List<Track> tempRoute = findRouteTo(stationDestination, new ArrayList<>());

                        if(tempRoute != null)
                        {
                            waitAtLight = false;
                            this.trackCurrentlyOn.getLight().setSignal(true, stationDestination.getLeftOrRight());
                        }
                        continue;
                    }
                    routeGood = true;

                    if (stationDestination.getNeighborTrack().equals(trackCurrentlyOn))
                    {
                        this.trackCurrentlyOn.setLocked(false);
                    }

                }
            }
        }
    }

    /**
     * findRouteTo will find a route to a given station called destination. It will
     * build a list of tracks that will be considered the route.
     * it first checks if any possible route is available ignoring any locks and
     * states, if so then it checks if it can get to the route while acknowledging locks
     * and states, if not waitAtLight... is called and a route is found to get to the
     * nearest light
     *
     * @param destination the station where we would like to go
     * @return the route we find, null if no route found
     */
    public synchronized List<Track> findRouteTo(Station destination, List<Track> route)
    {
        if(!validDestinationChoice(destination))    //error handling if user tries to go to station on same side
        {
            return null;
        }
        stationDestination = destination;

        if(destination.getLeftOrRight() == 1)
            trackCurrentlyOn.setPassToRight(true);
        else
            trackCurrentlyOn.setPassToRight(false);

        trackCurrentlyOn.resetVisitedTracks();
        List<Track> anyRoutePossible = trackCurrentlyOn.findAnyPossibleRoute(destination, route, destination.getLeftOrRight());
        if(anyRoutePossible == null)
        {
            trackCurrentlyOn.resetVisitedTracks();
            return null;
        }

        trackCurrentlyOn.resetVisitedTracks();
        List<Track> theRoute = trackCurrentlyOn.findRoute(destination, new ArrayList<>(), destination.getLeftOrRight());

        if(theRoute != null)
        {
            this.route = theRoute;
            trackCurrentlyOn.lockRoute(route);
            trackCurrentlyOn.resetVisitedTracks();
        }
        else if(!waitAtLight)   //theRoute is null and waitAtLight is false
        {
            trackCurrentlyOn.resetVisitedTracks();
            theRoute = waitAtLightUntilRouteGood(destination, new ArrayList<>(), destination.getLeftOrRight());
        }
        return theRoute;
    }

    /**
     * waitAtLightUntilRouteGood: called whenever there is a possible route to a station, but
     * it's currently not available. finds a route to the nearest light and waits until
     * route to desintation becomes available
     * @param destination station the train is wanting to go to
     * @param route a new empty list of tracks
     * @param rightOrLight whether our destination is on the right or left
     * @return the route found to the destination
     */
    public List<Track> waitAtLightUntilRouteGood(Station destination, List<Track> route, int rightOrLight)
    {
        List<Track> getToLightRoute = trackCurrentlyOn.findRouteToLight(route, rightOrLight);

        if(getToLightRoute != null)
        {
            waitAtLight = true;
            trackCurrentlyOn.lockRoute(getToLightRoute);
            trackCurrentlyOn.resetVisitedTracks();
            this.route = getToLightRoute;

            return getToLightRoute;
        }
        return null;
    }

    /**
     * arrivedAtDest: called when a train arrvies at it's
     * destination, sets some states
     */
    public synchronized void arrivedAtDest()
    {
        trackCurrentlyOn.resetVisitedTracks();
        stationDockedAt = stationDestination;
        routeGood = false;
    }

    /**
     * arrivedAtLight: when a train arrives at the light, arrived at light is
     * called to set some states
     */
    public synchronized void arrivedAtLight()
    {
        if(route.indexOf(trackCurrentlyOn) == route.size()-1)
        {
            routeGood = false;
            arrivedAtLight = true;
            trackCurrentlyOn.resetVisitedTracks();
        }
    }

    /**
     * validDestinationChoice: processed the given destination
     * and ensures it's a valid destination for the train to go to
     * @param destination station the train is trying to go to
     * @return true if valid station, false if invalid station
     */
    private boolean validDestinationChoice(Station destination)
    {
        if(stationDockedAt.getLeftOrRight() == 0)
        {
            if(destination.getLeftOrRight() == 1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if(destination.getLeftOrRight() == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    @Override
    public String toString()
    {
        return "Train"+trainId;
    }

    /********Getters and Setters ***********/

    synchronized public Track getTrackCurrentlyOn() { return trackCurrentlyOn; }
    synchronized public void setTrackCurrentlyOn(Track trackCurrentlyOn)
    {
        this.trackCurrentlyOn = trackCurrentlyOn;
    }
    public void setTrainGuiX(double x) { this.guiX = x;}
    public double getTrainGuiX() { return guiX; }
    public void setTrainGuiY(double y) { this.guiY = y;}
    public double getTrainGuiY() { return guiY; }
    public boolean getWaitAtLight() { return waitAtLight; }
    public boolean getAnimationFinished() { return animationFinished; }
    public void setAnimationFinished(boolean expr) { animationFinished = expr; }

    public double getTempX()
    {
        return tempX;
    }

    public void setTempX(double tempX)
    {
        this.tempX = tempX;
    }

    public double getTempY()
    {
        return tempY;
    }

    public void setTempY(double tempY)
    {
        this.tempY = tempY;
    }

    public double getStepX()
    {
        return stepX;
    }

    public void setStepX(double stepX)
    {
        this.stepX = stepX;
    }

    public double getStepY()
    {
        return stepY;
    }

    public void setStepY(double stepY)
    {
        this.stepY = stepY;
    }

    public Station getStationDestination()
    {
        return stationDestination;
    }

    public Station getStationDockedAt()
    {
        return stationDockedAt;
    }

    public boolean getArrivedAtLight() { return arrivedAtLight; }
    public boolean getRouteGood() { return routeGood; }
}
