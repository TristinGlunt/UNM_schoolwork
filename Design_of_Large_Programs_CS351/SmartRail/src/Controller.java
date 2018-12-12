import javafx.geometry.Point2D;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

/**
 * =========================================
 *
 * SmartRail - CS 351 Project 3
 * @author Tristin Glunt | tglunt@unm.edu
 * @author Duong Nguyen | dnguyen@unm.edu
 * 11/16/17
 * =========================================
 */

public class Controller
{
    private ViewManager viewManager;
    private Train currentTrain;
    private Object syncObject;

    private List<List<Point2D>> rails = new ArrayList<>();
    private ArrayList<Station> stations = new ArrayList<>();
    private ArrayList<Switch> setOfSwitches = new ArrayList<>();
    private ArrayList<Track> setOfDiagonalTracks = new ArrayList<>();
    private ArrayList<Train> trains = new ArrayList<>();

    private int counter = 0;

    public Controller(ViewManager viewManager, Object syncObject)
    {
        this.viewManager = viewManager; //has access to the GUI, but the GUI doesn't have direct access to the Controller
        this.syncObject = syncObject;
    }

    public void start()
    {
        readXml();

        /* once the XML file has been completely read and the rails have been created we pass the needed information
            down to the GUI
         */
        viewManager.setCounter(counter);// set number of tracks and stations on a rail; same for all rail
        viewManager.setRails(rails);
        viewManager.createRails();
        viewManager.setFindRoute(j -> findRoute(j));    //lambda functions for the GUI to call based on listeners
        viewManager.setPlaceTrainAtStation(k -> placeTrainAtStation(k));
        viewManager.setCurrentTrain(t -> setCurrentTrain(t));
        viewManager.promptForTrainPlacement();
        viewManager.animateGUI();
    }

    /**
     * readXml: reads our XML file and builds the rail as appropriate. This creates all of our logic components.
     * This function does not link any of them together as there are many more if statements and cases for that.
     * So, once the XML file is done being read, linkRails is called to link the components following
     * the rules in the spec on how tracks can only know their neighbors.
     */
    private void readXml()
    {
        double x, y;
        boolean done = false;
        int name = 'a';

        try
        {
            MapLayout map = new MapLayout();
            Document doc = map.getMap();
            doc.getDocumentElement().normalize();
            NodeList rails = doc.getElementsByTagName("Rail");

            for (int i = 0; i < rails.getLength(); i++)
            {
                Element element = (Element) rails.item(i);
                NodeList components = (NodeList) element;

                /* a new rail start here */
                List<Point2D> rail = new ArrayList<>();
                Track trackOn = null;
                Station station = null;
                for (int j = 0; j < components.getLength(); j++)
                {
                    Node component = components.item(j);

                    if (component.getNodeType() == Node.ELEMENT_NODE)// get node only, not text type #text
                    {
                        x = Double.parseDouble(((NodeList) component).item(0).getTextContent());
                        y = Double.parseDouble(((NodeList) component).item(1).getTextContent());

                        if (component.getNodeName().equals("Light"))
                        {
                            Light light = new Light(x, y);

                            rail.add(new Light(x, y));
                        } else if (component.getNodeName().equals("Switch"))
                        {
                            rail.add(new Switch(x, y, trackOn, null));
                        }
                        else if (component.getNodeName().equals("Track"))
                        {
                            trackOn = new Track(x, y, syncObject);
                            rail.add(trackOn);
                            if (!done) counter++;
                        } else if (component.getNodeName().equals("Station"))
                        {
                            station = new Station(x, y, null);
                            stations.add(station);
                            station.setLeftOrRight(0);
                            if (x != 0) station.setLeftOrRight(1);

                            rail.add(station);
                            if (!done) counter++;
                        }
                        name++;
                    }
                }

            /* add a new rail to the rails */
                this.rails.add(rail);
                done = true;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        linkRails();
    }

    /**
     * linkRails: links the components together (stations, tracks, lights switches), following the rules from the Spec.
     * Assigns the correct neighbors to the different components, works for any given rail system as long as it follows
     * the rules given by the README. There were lots of different possibilities of how a map was setup,
     * so this is quite a complicated function with many if statements.
     */
    private void linkRails()
    {
        Track lastTrack = null;
        Point2D previousNode = null;
        Point2D nextNode = null;

        boolean firstIteration = true;


        for (int m = 0; m < rails.size(); m++)
        {
            firstIteration = true;
            for(int i = 0; i < rails.get(m).size(); i++)
            {
                if(!firstIteration)
                    previousNode = rails.get(m).get(i-1);
                firstIteration = false;

                Point2D currentNode = rails.get(m).get(i);
                if(i != rails.get(m).size() - 1)
                    nextNode = rails.get(m).get(i+1);

                if (currentNode.toString().startsWith("Switch"))
                {
                    setOfSwitches.add((Switch)currentNode); //we add all switches read in to this set of switches
                    ((Switch) currentNode).trackOn = (Track) previousNode;
                }
                if (currentNode.toString().startsWith("Light"))
                {
                    ((Light) currentNode).setMyTrack((Track) previousNode);     //last item in list will be track for light
                }
                if (currentNode.toString().startsWith("Station"))
                {
                    if (((Station) currentNode).getLeftOrRight() == 1)
                    {
                        ((Station) currentNode).setNeighborTrack((Track) previousNode);
                    }
                    else  //left station
                    {
                        ((Station) currentNode).setNeighborTrack((Track) nextNode);
                    }
                }
                if (currentNode.toString().startsWith("Track"))
                {
                    if(rails.get(m).size() < 4) //we set our minumum number of tracks in a rail here so we can continue to detect diagonal tracks from the XML file
                    {
                        setOfDiagonalTracks.add((Track)currentNode);
                    }
                    else if(previousNode.toString().startsWith("Switch") || previousNode.toString().startsWith("Light"))
                    {
                        lastTrack.setRightNeighbor((Track) currentNode);

                        if(nextNode.toString().startsWith("Switch"))
                        {
                            ((Track) currentNode).passTrackInfo(lastTrack, (Track)rails.get(m).get(i+2), null,
                                    false, false, null, (Switch) nextNode, null, null);
                            lastTrack = (Track)currentNode;
                        }
                        else if(nextNode.toString().startsWith("Light"))
                        {
                            ((Track) currentNode).passTrackInfo(lastTrack, (Track)rails.get(m).get(i+2), null,
                                    false, false, (Light) nextNode, null, null, null);
                            lastTrack = (Track)currentNode;
                        }
                        else if(nextNode.toString().startsWith("Station"))
                        {
                            ((Track)currentNode).passTrackInfo(lastTrack, null, (Station)nextNode,
                                    false, true, null, null, null, null);
                        }
                        else
                        {
                            ((Track) currentNode).passTrackInfo(lastTrack, (Track) nextNode, null,
                                    false, false, null, null, null, null);
                        }
                    }
                    else if (nextNode.toString().startsWith("Switch"))
                    {
                        lastTrack = (Track) currentNode;
                        ((Track) currentNode).passTrackInfo((Track) previousNode, null, null, false,
                                false, null, (Switch) nextNode, null, null);
                    }
                    else if (nextNode.toString().startsWith("Station"))
                    {
                        lastTrack = (Track) currentNode;
                        ((Track) currentNode).passTrackInfo((Track) previousNode, null, (Station) nextNode, false,
                                true, null, null, null, null);

                    }
                    else if (nextNode.toString().startsWith("Light"))
                    {
                        lastTrack = (Track) currentNode;
                        ((Track) currentNode).passTrackInfo((Track) previousNode, null, null, false,
                                false, (Light) nextNode, null, null, null);

                    }
                    else if(previousNode.toString().startsWith("Station"))
                    {
                        ((Track) currentNode).passTrackInfo(null, (Track) nextNode, (Station) previousNode,
                                true, false, null, null, null, null);
                    }
                    else
                    {
                        ((Track) currentNode).passTrackInfo((Track) previousNode, (Track) nextNode, null,
                                false, false, null, null, null, null);
                    }
                }
            }
        }
        linkDiagonalTracksAndSwitches();
    }

    /**
     * linkDiagonalTracksAndSwitches: after the entire map layout
     * file has been read in, we can now loop through the diagonal tracks
     * and link them with their respective switches
     */
    private void linkDiagonalTracksAndSwitches()
    {
        for(Track diagTrack : setOfDiagonalTracks)
        {
            Switch topSwitch = findCorrectSwitch(diagTrack, true);
            Switch bottSwitch = findCorrectSwitch(diagTrack, false);

            diagTrack.passTrackInfo(null, null, null,
                    false, false, null, topSwitch, bottSwitch, null);

            topSwitch.setDiagonalTrack(diagTrack);
            bottSwitch.setDiagonalTrack(diagTrack);
        }
    }

    /**
     * findCorrectBottomSwitch: loops through the set of switches and finds
     * the correct bottom switch that lines up with the diagonalTrack being passed
     * @param diagonalTrack that will be linked with a bottom switch
     * @param topOrBottom top is true false is bottom
     * @return the bottom switch that matches the placement for the diagonal track
     */
    private Switch findCorrectSwitch(Track diagonalTrack, boolean topOrBottom)
    {
        double x = diagonalTrack.getX();
        double y = diagonalTrack.getY();

        for(Switch step : setOfSwitches)
        {
            double tempX = step.getX();
            double tempY = step.getY();

            if(tempX + 1 == x || tempX - 1 == x)
            {
                if(!topOrBottom)
                {
                    if(tempY + 1 == y)
                        return step;
                }
                else
                    if(tempY-1 == y)
                        return step;
            }
        }
        return null;
    }

    /**
     * placeTrainAtStation: given the input from the user
     * to place a train at a certain station, the controller placeTrainAtStation
     * is called to place a train in the logic
     * @param station  the station receiving a train
     * @return  true if its a valid train placement and the placement happened
     */
    private boolean placeTrainAtStation(String station)
    {
        for(Station step : stations)
        {
            if(step.toString().startsWith("Station" + station))
            {
                if(step.getNeighborTrack().isTrainOnMe())
                {
                    viewManager.setValidStation(false);
                    return false;
                }

                Train train = new Train(step.getNeighborTrack().getX(), step.getNeighborTrack().getY(),
                        step.getNeighborTrack(),null, this.syncObject, step);   //place train on raile

                step.getNeighborTrack().setTrain(train);    //place train on track

                trains.add(train);
                viewManager.setValidStation(true);
                viewManager.setTrains(trains);

                return true;
            }
        }
        viewManager.setValidStation(false);
        return false;
    }

    /**
     * when a train is clicked on in the GUI,
     * currentTrain is updated to that current train
     * @param currTrain the train that has been clicked on
     */
    private void setCurrentTrain(String currTrain)
    {
        for(Train train : trains)
        {
            if(train.toString().equals(currTrain))
            {
                currentTrain = train;
            }
        }
    }

    /**
     * findRoute: Takes a given destination and instructs the train to find a route to that destination,
     * passes the found route to the GUI so it knows if it's found or not, //TODO change to boolean value so GUI doesnt access to ROUTE
     * @param destination the destination given from the GUI input, the popupbox to be specific
     */
    private void findRoute(String destination)
    {
        if(Integer.parseInt(destination) > stations.size() - 1)
        {
            viewManager.setStationDoesntExist(true);
            return;
        }
        if(checkDeadLock(stations.get(Integer.parseInt(destination))))
        {
            viewManager.setRouteFound(null);
            return;
        }

        //find route for latest train added
        List<Track> logRoute = currentTrain.findRouteTo(stations.get(Integer.parseInt(destination)), new ArrayList<>());
        viewManager.setRouteFound(logRoute);    //can animate train over entire route if need be
    }

    /**
     * checkDeadLock:
     * loops through the trains, checks if a station that a train is
     * requesting to go to also has a train requesting to go the station the
     * requesting train is docked at
     * @param destination the requesting trains destination
     * @return true if there is a deadlock possible, false if not
     */
    private boolean checkDeadLock(Station destination)
    {
        for(Train step : trains)
        {
            if(step.getStationDestination() != null)
            {
                if(step.getStationDockedAt().toString().equals(destination.toString()))
                {
                    if(step.getStationDestination().toString().equals(currentTrain.getStationDockedAt().toString()))
                        return true;
                }
            }
        }
        return false;
    }
}