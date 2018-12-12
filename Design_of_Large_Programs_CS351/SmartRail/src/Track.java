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
public class Track extends Point2D implements Runnable
{
    private Light light;
    private Station neighboringStation;
    private String trackName;
    private Switch topSwitch;
    private Switch bottomSwitch;
    private Track leftNeighbor;
    private Track rightNeighbor;
    private Train train;
    private Object syncObject;

    private boolean startTrack;
    private boolean endTrack;
    private boolean trainOnMe;
    private boolean passToRight;
    private boolean trackVisited = false;
    private boolean locked = false;

    private static int trackNum = 1;
    private static ArrayList<Track> visitedTracks = new ArrayList<>();

    public Track(double x,double y, Object syncObject)
    {
        super(x, y);
        this.syncObject = syncObject;
        Thread track = new Thread(this);
        track.setName(Integer.toString(trackNum));
        trackName = "track " + Integer.toString(trackNum);
        trackNum++;
        track.start();
    }

    /**
     * passTrackInfo: called once we start linking the components in Controller.
     * These are the vars that each track knows, mainly it's left and right neighbors,
     * if it has a train, or any other components
     * @param leftNeighbor track to the left, if no traack then null
     * @param rightNeighbor track to the right, if no track then null
     * @param neighboringStation if one of our neighbors is a station
     * @param startTrack start
     * @param endTrack end
     * @param light component
     * @param topSwitch component
     * @param bottomSwitch for diago tracks
     * @param train train
     */
    public void passTrackInfo(Track leftNeighbor, Track rightNeighbor, Station neighboringStation,
                              boolean startTrack, boolean endTrack, Light light, Switch topSwitch, Switch bottomSwitch,
                              Train train)
    {
        this.leftNeighbor = leftNeighbor;
        this.rightNeighbor = rightNeighbor;
        this.neighboringStation = neighboringStation;   //might not need neighboring station?
        this.startTrack = startTrack;
        this.endTrack = endTrack;
        this.light = light;
        this.topSwitch = topSwitch;
        this.bottomSwitch = bottomSwitch;
        this.train = train;
        trainOnMe = (train != null);
        if(trainOnMe && startTrack)
            passToRight = true;

    }

    /**
     * run: Every track is it's own thread. The tracks run, if they have a train on them
     * they might do some processing but first they wait, otherwise they wait until they have a train on them.
     * Once a track with a train on it is woken up, the track checks if the train that is on it has a route,
     * if it does and trainsLaunch has been toggled from the GUI, it will check if it can pass the train.
     * If it can pass the train, it will passTheTrain right or left depending on the boolean condition.
     */
    @Override
    public void run()
    {
        while(true)
        {
            if(trainOnMe) //only go into if statement if the train is on current track
            {
                synchronized (syncObject)
                {
                    try
                    {
                        syncObject.wait(); //wait until notified by AnimationTimer in main to check if route is good
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    //if route is good found by the train
                    if(train.getRouteGood())
                    {
                        if (ViewManager.trainsLaunch)
                        {
                            if(train.getWaitAtLight())
                            {
                                if(light != null)
                                {
                                    train.arrivedAtLight();
                                    if(train.getArrivedAtLight())
                                        continue;
                                }
                            }

                            if (neighboringStation == train.getStationDestination())
                            {
                                train.arrivedAtDest();
                            }
                            else if (passToRight) //if we're passing to the right
                            {
                                passTrain(true);
                            }
                            else
                            {
                                passTrain(false);
                            }
                            Main.globalVariable--;
                        }
                    }
                }
            }
            else //otherwise we will wait until notified by the train
            {
                synchronized (syncObject)
                {
                    try
                    {
                        syncObject.wait();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * receive the train from another track, update the trains
     * current track on to this track. If passToRight is true,
     * that means we will continue to pass the train to the right, otherwise
     * passToRight will be false and we'll continually pass to the left
     * @param train the train that this track is receiving it
     * @param passToRight the boolean cond. whether we're passing to the right or left
     */
    public void receiveTrain(Train train, boolean passToRight)
    {
        this.train = train;
        this.passToRight = passToRight;
        trainOnMe = true;
        train.setTrackCurrentlyOn(this);
    }

    /**
     * passTrain: passes the train to it's neighbor depending on passToRight. This checks
     * whether we'll be passing to a diagonal track, from a diagonal track, or a regular
     * horizontral track. this method unlocks tracks as it passes them and resets
     * states of components to default once the train has been passed.
     * @param passToRight the cond. informing us whether we're passing to the left or right
     */
    private void passTrain(boolean passToRight)
    {
        Track neighbor;
        if(passToRight)
            neighbor = rightNeighbor;
        else
            neighbor = leftNeighbor;

        this.trackVisited = true;
        this.locked = false;

        if(topSwitch != null && bottomSwitch != null)   //indicator of a diagonal track
        {
            if(bottomSwitch.getState() == 1 && !bottomSwitch.trackOn.trackVisited)  //indicator to pass down right
                bottomSwitch.passTrainToTrackOn(train, passToRight);
            else if(topSwitch.getState() == 1 && !topSwitch.trackOn.trackVisited)   //indicator to pass train up right
                topSwitch.passTrainToTrackOn(train, passToRight);

            bottomSwitch.setState(0);
            topSwitch.setState(0);
            bottomSwitch.setRouteLock(false);
            topSwitch.setRouteLock(false);
            train = null;
            trainOnMe = false;
        }
        else if(topSwitch != null)                                      //horizontal track with switch on it
        {
            if(topSwitch.getState() == 0)
            {
                if(neighbor != null)
                {
                    neighbor.receiveTrain(train, passToRight);
                }
            }
            else
            {
                topSwitch.passTrainToDiagonalTrack(train, passToRight);
                topSwitch.setState(0);              //turn switch off after passing it
                topSwitch.setRouteLock(false);
            }

            train = null;
            trainOnMe = false;
        }
        else
        {
            if(light != null)
            {
                light.setSignal(false, 0);
                light.setLocked(false);
            }
            //horizontal track with no switch
            neighbor.receiveTrain(train, passToRight);
            train = null;
            trainOnMe = false;
        }
    }

    /**
     * findRoute: the main finding route algorithm that follows the rules laid out by the track.
     * FindRoute will acknowledge locked tracks, lights, switches, and will not ignore there states
     * like findAnyPossRoute will. This is the main algorithm used to find routes when findAnyPossRoute
     * has returned true. It's a recursive DFS like algorithm, prioritizing horizontal tracks before
     * traveling down diagonal tracks
     * @param destination the station the train has requested to go to
     * @param route the route that we're building up to get from the starting track to the destination
     * @param leftOrRight whether or not our station is on the left or right
     * @return the route that is found
     */
    public List<Track> findRoute(Station destination, List<Track> route, int leftOrRight)
    {
        List<Track> possRout2 = null;

        if(locked) return null; //we do not want other trains interfering with tracks that are currently locked

        if(trackVisited) return null;

        if(trainOnMe && route.size() > 1)
            return null;

        if(light != null)
        {
            light.setSignal(true, leftOrRight);
            light.setLocked(true);
        }

        route.add(this);
        trackVisited = true;
        visitedTracks.add(this);

        if(endTrack && (leftOrRight == 1))
        {
            if(neighboringStation == destination)
            {
                if(trainOnMe)
                    return null;
                return route;
            }
            else
            {
                route.remove(this);
                return null;
            }
        }
        else if(startTrack && (leftOrRight == 0))
        {
            if (neighboringStation == destination)
            {
                if(trainOnMe)
                    return null;
                return route;
            }
            else
            {
                route.remove(this);
                return null;
            }
        }
        else
        {
            //on a diagonal track, can pass to bottom for now but will need to implement passing to top as well
            if(this.topSwitch != null && this.bottomSwitch != null)
            {
                //recursive call for the diagonal track to pass to the track one of it's switches are on
                if(!this.bottomSwitch.trackOn.trackVisited)
                {
                    possRout2 = this.bottomSwitch.passRouteToTrackOn(destination, route, leftOrRight);
                }
                else if(!this.topSwitch.trackOn.trackVisited)
                {
                    possRout2 = this.topSwitch.passRouteToTrackOn(destination, route, leftOrRight);
                }
                if(possRout2 != null)
                {
                    topSwitch.setState(1);      //if route is good using diag track turn switches on
                    bottomSwitch.setState(1);
                    topSwitch.setRouteLock(true);
                    bottomSwitch.setRouteLock(true);

                    return possRout2; //if the route was good return continue returning the route
                }
                else
                {
                    topSwitch.setState(0);      //if the route was bad and we have returned all the way back
                    bottomSwitch.setState(0);   //to a diag. track set the switches back to 0
                    topSwitch.setRouteLock(false);
                    bottomSwitch.setRouteLock(false);

                    route.remove(this); //otherwise remove the diagonal track and return null
                    return null;
                }
            }
            else if(leftOrRight == 1)
            {
                //recursive call to pass route finding down to horizontal tracks
                List<Track> routeFlag = rightNeighbor.findRoute(destination, route, leftOrRight);
                if (routeFlag == null)
                {
                    if ((this.topSwitch != null && !this.topSwitch.getDiagonalTrack().trackVisited)
                            && (!(this.topSwitch.getDiagonalTrack().getX() > getX() && leftOrRight == 0)
                            && (!(this.topSwitch.getDiagonalTrack().getX() < getX() && leftOrRight == 1))))
                    {
                        //recursive call to go down diagonal track
                        List<Track> possRoute = this.topSwitch.passRouteToDiagTrack(destination, route, leftOrRight);
                        if (possRoute != null)
                        {
                            return possRoute;   //if the switched track lead us to our dest. return the right route
                        }
                    }

                    if(light != null)
                    {
                        light.setSignal(false, leftOrRight);
                        light.setLocked(false);
                    }
                    route.remove(this);     //remove horizontal tracks when routeFlag was returned as null
                    return null;               //route flag will return null when the path was wrong
                }                               //this case also catches tracks that received the path from a diag track
                return routeFlag;
            }
            else
            {
                //recursive call to pass route finding down to horizontal tracks
                List<Track> routeFlag = leftNeighbor.findRoute(destination, route, leftOrRight);
                if (routeFlag == null)
                {
                    if ((this.topSwitch != null && !this.topSwitch.getDiagonalTrack().trackVisited)
                            && (!(this.topSwitch.getDiagonalTrack().getX() > getX() && leftOrRight == 0)
                            && (!(this.topSwitch.getDiagonalTrack().getX() < getX() && leftOrRight == 1))))
                    {
                        List<Track> possRoute = this.topSwitch.passRouteToDiagTrack(destination, route, leftOrRight); //recursive call to go down diagonal track
                        if (possRoute != null)
                        {
                            return possRoute;   //if the switched track lead us to our dest. return the right route
                        }
                    }

                    if(light != null)
                    {
                        light.setSignal(false, leftOrRight);
                        light.setLocked(false);
                    }
                    route.remove(this);     //remove horizontal tracks when routeFlag was returned as null
                    return null;               //route flag will return null when the path was wrong
                }                               //this case also catches tracks that received the path from a diag track
                return routeFlag;
            }
        }
    }


    /**
     * findAnyPossiblyRoute is a variation of finding a route. This algorithm ignores if
     * tracks, lights or switches are locked. It also doesn't change any states of any component.
     * The purpose of this algorithm is to just confirm that a route doest exist to the given
     * station
     * @param destination station train is wanting to go
     * @param route route we're going to be building up
     * @param leftOrRight whether our destination is on the left or right
     * @return null if no route possible, or list of tracks that make up our route
     */
    public List<Track> findAnyPossibleRoute(Station destination, List<Track> route, int leftOrRight)
    {
        List<Track> possRout2 = null;

        if(trackVisited) return null;

        route.add(this);
        trackVisited = true;
        visitedTracks.add(this);

        if(endTrack && (leftOrRight == 1))
        {
            if(neighboringStation == destination)
            {
                return route;
            }
            else
            {
                route.remove(this);
                return null;
            }
        }
        else if(startTrack && (leftOrRight == 0))
        {
            if (neighboringStation == destination)
            {
                return route;
            }
            else
            {
                route.remove(this);
                return null;
            }
        }
        else
        {
            if(this.topSwitch != null && this.bottomSwitch != null)
            {
                //recursive call for the diagonal track to pass to the track one of it's switches are on
                if(!this.bottomSwitch.trackOn.trackVisited)
                {
                    possRout2 = this.bottomSwitch.passRouteToTrackOnAnyPoss(destination, route, leftOrRight);
                }
                else if(!this.topSwitch.trackOn.trackVisited)
                {
                    possRout2 = this.topSwitch.passRouteToTrackOnAnyPoss(destination, route, leftOrRight);
                }
                if(possRout2 != null)
                {
                    return possRout2; //if the route was good return continue returning the route
                }
                else
                {
                    route.remove(this); //otherwise remove the diagonal track and return null
                    return null;
                }
            }
            else if(leftOrRight == 1)
            {
                //recursive call to pass route finding down to horizontal tracks
                List<Track> routeFlag = rightNeighbor.findAnyPossibleRoute(destination, route, leftOrRight);
                if (routeFlag == null)
                {
                    if ((this.topSwitch != null && !this.topSwitch.getDiagonalTrack().trackVisited)
                            && (!(this.topSwitch.getDiagonalTrack().getX() > getX() && leftOrRight == 0)
                            && (!(this.topSwitch.getDiagonalTrack().getX() < getX() && leftOrRight == 1))))
                    {
                        //recursive call to go down diagonal track
                        List<Track> possRoute = this.topSwitch.passRouteToDiagTrackAnyPoss(destination, route, leftOrRight);
                        if (possRoute != null)
                        {
                            return possRoute;   //if the switched track lead us to our dest. return the right route
                        }
                    }
                    route.remove(this);     //remove horizontal tracks when routeFlag was returned as null
                    return null;               //route flag will return null when the path was wrong
                }                               //this case also catches tracks that received the path from a diag track
                return routeFlag;
            }
            else
            {
                //recursive call to pass route finding down to horizontal tracks
                List<Track> routeFlag = leftNeighbor.findAnyPossibleRoute(destination, route, leftOrRight);
                if (routeFlag == null)
                {
                    if ((this.topSwitch != null && !this.topSwitch.getDiagonalTrack().trackVisited)
                            && (!(this.topSwitch.getDiagonalTrack().getX() > getX() && leftOrRight == 0)
                            && (!(this.topSwitch.getDiagonalTrack().getX() < getX() && leftOrRight == 1))))
                    {
                        //recursive call to go down diagonal track
                        List<Track> possRoute = this.topSwitch.passRouteToDiagTrackAnyPoss(destination, route, leftOrRight);
                        if (possRoute != null)
                        {
                            return possRoute;   //if the switched track lead us to our dest. return the right route
                        }
                    }
                    route.remove(this);     //remove horizontal tracks when routeFlag was returned as null
                    return null;               //route flag will return null when the path was wrong
                }                               //this case also catches tracks that received the path from a diag track
                return routeFlag;
            }
        }
    }

    /**
     * findRouteToLight: another variation of the algorithm to finding destinations,
     * findRouteToLight only finds a route to the nearest light. this is called
     * when findRoute can't find a route, but findAnyPossRoute can
     * @param route route we're building up
     * @param leftOrRight whether our destination is on the left or right
     * @return null if no route found, or tracks that make up our route
     */
    public List<Track> findRouteToLight(List<Track> route, int leftOrRight)
    {
        if(trackVisited)
            return null;

        trackVisited = true;
        visitedTracks.add(this);

        route.add(this);

        if(light != null && !light.getSignal() && !light.getLocked())   //if this track has a light, the light is red and the light is not locked
        {
            light.setSignal(false, leftOrRight);
            light.setLocked(true);
            return route;
        }
        else
        {
            if(leftOrRight == 1)
            {
                List<Track> routeFlag = rightNeighbor.findRouteToLight(route, leftOrRight);
                if (routeFlag == null)
                {
                    route.remove(this);
                    return null;
                }
                return routeFlag;
            }
            else
            {
                List<Track> routeFlag = leftNeighbor.findRouteToLight(route, leftOrRight);
                if (routeFlag == null)
                {
                    route.remove(this);
                    return null;
                }
                return routeFlag;
            }
        }
    }

    /**
     * lockRoute: loops through the route passed in and locks each track
     * @param routeToLock the route we're locking
     */
    public synchronized void lockRoute(List<Track> routeToLock)
    {
        for(Track step : routeToLock)
        {
            step.locked = true;
        }
    }

    /**
     * resetVisitedTracks: loops through
     * the set of visitedTracks and resets them to false, regardless
     * of which track is resetting them
     */
    public void resetVisitedTracks()
    {
        for(Track step : visitedTracks)
        {
            step.trackVisited = false;
        }
    }

    /**
     * toString: how we identify each track and match them to their GUI component
     * @return the ID of the track
     */
    @Override
    public String toString()
    {
        return "Track"+hashCode();
    }


    /********Getters and Setters********/
    public Light getLight() { return light; }
    public Switch getTopSwitch() { return topSwitch; }
    public Switch getBottomSwitch() { return bottomSwitch; }
    public void setRightNeighbor(Track rightNeighbor)
    {
        this.rightNeighbor = rightNeighbor;
    }
    public void setTrain(Train train) { this.train = train; trainOnMe = true;}
    public void setPassToRight(boolean passToRight) { this.passToRight = passToRight; }
    public void setLocked(boolean expr) { this.locked = expr; }
    public boolean getLocked() { return locked; }
    public boolean isTrainOnMe() { return trainOnMe; }

}
