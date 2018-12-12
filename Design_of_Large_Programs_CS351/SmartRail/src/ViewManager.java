import com.sun.deploy.util.StringUtils;
import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * =========================================
 *
 * SmartRail - CS 351 Project 3
 * @author Tristin Glunt | tglunt@unm.edu
 * @author Duong Nguyen | dnguyen@unm.edu
 * 11/16/17
 * =========================================
 */

public class ViewManager extends Pane
{
    private double WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
    private double HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    private double width, height;
    private int counter;
    private int numOfTrains;

    private List<List<Point2D>> rails;
    private List<Track> routeFound;
    private List<Button> setOfComp = new ArrayList<>();
    private List<Point2D> setOfLogicComp = new ArrayList<>();
    private ArrayList<Train> trains = new ArrayList<>();
    private Object syncObject;

    private String dockedStation = null;
    private String destination = null;
    private Consumer<String> findRoute;
    private Consumer<String> placeTrainAtStation;
    private Consumer<String> currentTrainSetter;
    private Label trainGoState;

    private boolean dialogBoxIsUp = false;
    private boolean validStation;
    private boolean stationDoesntExist;
    public static boolean trainsLaunch;


    /**
     * constructor for view manager, passed the global syncobject for all objects to wait and wake up on
     * creates the listener for the state for trains to go or not
     * @param syncObject
     */
    public ViewManager(Object syncObject)
    {
        setMinSize(WIDTH, HEIGHT);
        this.syncObject = syncObject;
        tellTrainsToGoListener();
    }

    /**
     * animateGUI: animation timer that has a 25ms delay from 60fps,
     * continously loops through all of the logic components and updates
     * the GUI components depending if their state changes.
     *
     * Also controls flow of the logic and GUI to be synced by calling
     * animateTrain, which then calls notifyAll, waking up the threads
     * to attempt to pass the train/find routes etc
     */
    public void animateGUI()
    {
        new AnimationTimer()
        {
            long lastUpdate = 0;
            @Override
            public void handle(long now)
            {
                if (now - lastUpdate >= 320_000_000)
                {
                    lastUpdate = now;

                    for (Point2D logComp : setOfLogicComp)
                    {
                        for (Button curr : setOfComp)
                        {
                            if (logComp.toString().equals(curr.getId()))
                            {
                                if (logComp.toString().startsWith("Train"))
                                {
                                    try
                                    {
                                        animateTrain(logComp, curr);
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                                if (logComp.toString().startsWith("Switch"))
                                {
                                    if (((Switch) logComp).getState() == 1)
                                        curr.setStyle("-fx-background-color: gold;" +
                                                "-fx-border-color: black");
                                    else
                                        curr.setStyle("-fx-background-color: sienna;" +
                                                "-fx-border-color: black");
                                }
                                if (logComp.toString().startsWith("Light"))
                                {
                                    if (((Light) logComp).getSignal())
                                    {
                                        //green on left, red on right
                                        if(((Light)logComp).isGoingRight() == 0)
                                        {
                                            curr.setStyle("-fx-background-color: linear-gradient(red 1%, red 50%, limegreen 50%, limegreen 100%);" +
                                                    "-fx-border-color: black;" +
                                                    "-fx-border-width: 2;");
                                        }
                                        else
                                        {
                                            curr.setStyle("-fx-background-color: linear-gradient(limegreen 0%, limegreen 50%, red 50%, red 100%);" +
                                                    "-fx-border-color: black;" +
                                                    "-fx-border-width: 2;");
                                        }
                                    }
                                    else
                                    {
//                                      //green on right, red on left
                                        curr.setStyle("-fx-background-color: linear-gradient(red 1%, red 50%, red 50%, red 99%);" +
                                                "-fx-border-color: black;" +
                                                "-fx-border-width: 2");
                                    }
                                }
                                if (logComp.toString().startsWith("Track"))
                                {
                                    if (((Track) logComp).getLocked())
                                        curr.setStyle("-fx-opacity: 1");
                                    else if(((Track)logComp).isTrainOnMe())
                                        curr.setStyle("-fx-opacity: 1");
                                    else
                                        curr.setStyle("-fx-opacity: 0.25");
                                }
                            }
                        }
                    }
                }
            }
        }.start();
    }


    /**
     * animateTrain: moves the train on the GUI corresponding to
     * the logic train object coordinates. continously called from
     * the AnimationTimer animateGUI.
     * @param trainLog logic train object that corresponds to the GUI train
     * @param trainButt GUI train that corresponds to the logic train, continously polls logic train to
     *                  determine coordinates
     * @throws InterruptedException
     */
    public void animateTrain(Point2D trainLog, Button trainButt) throws InterruptedException
    {
        synchronized (syncObject)
        {
            Train train = (Train)trainLog;
            syncObject.notifyAll();

            if(train.getAnimationFinished())
            {
                double trainOldX = train.getTrainGuiX();
                double trainOldY = train.getTrainGuiY();

                double newX = (train.getTrackCurrentlyOn().getX() * width) + (0.15 * width);
                double newY = (train.getTrackCurrentlyOn().getY() * height) + (0.25 * height);

                if ((newX != trainOldX) || (newY != trainOldY)) //if the track the train is on is different coordinates
                {                                               // then the the trains, animate the train
                    train.setAnimationFinished(false);

                    train.setTempX(trainOldX);
                    train.setTempY(trainOldY);
                    train.setStepX((newX-trainOldX) / 15);

                    if (newY != trainOldY)
                    {
                        train.setStepY((newY-trainOldY) / 15);
                    } else
                        train.setStepY(0);

                    new AnimationTimer()
                    {
                        public void handle(long now)
                        {
                            if (((train.getTempX() - newX) < 5) && ((train.getTempY() - newY) < 5)
                                    && ((train.getTempX() - newX) >= -2) && ((train.getTempY() - newY) >= -2))
                            {
                                train.setAnimationFinished(true);
                                stop();
                            }
                            train.setTempX(train.getTempX()+train.getStepX());
                            train.setTempY(train.getTempY()+train.getStepY());
                            trainButt.setLayoutX(train.getTempX());
                            trainButt.setLayoutY(train.getTempY());
                        }
                    }.start();

                    train.setTrainGuiY(newY);
                    train.setTrainGuiX(newX);
                }
            }

            drawTrain(trainButt, trainLog);

            Main.globalVariable = 1;
            counter = 0;
        }

    }

    /**
     * continously draws the train image so it can flip the train properly
     * once it reaches a new station
     * @param trainButt train button for gui corresponding to trainlog
     * @param trainLog  train logic object
     */
    private void drawTrain(Button trainButt, Point2D trainLog)
    {
        if (((Train) trainLog).getStationDockedAt().getLeftOrRight() == 0)
        {
            trainButt.setStyle("-fx-background-image: url(file:resources/toytrain.png);" +
                    "-fx-background-size: 33px 33px;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-position: center;" +
                    "-fx-background-color: transparent;" +
                    "-fx-border: black; " +
                    "-fx-scale-x: -1;");
        } else
        {
            trainButt.setStyle("-fx-background-image: url(file:resources/toytrain.png);" +
                    "-fx-background-size: 33px 33px;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-position: center;" +
                    "-fx-background-color: transparent;" +
                    "-fx-scale-x: 1;");
        }
    }


    /**
     * createRails: creates all of the rails for the GUI,
     * mainly does this by calling create component and creating the corresponding
     * GUI component for the logic component
     */
    public void createRails()
    {

        width = WIDTH/counter;
        height = HEIGHT/16;
        trainGoState.setLayoutX(8 * width);
        trainGoState.setLayoutY(1.2 * height);

        for(List<Point2D> rail : rails)
        {
            for(Point2D component : rail)
            {
                setOfLogicComp.add(component);
                createComponent(component);
            }
        }
    }

    /**
     * createComponent: creates a GUI object corresponding
     * to the component it's passed. each component is handled slightly differently
     * with different images etc.
     * @param component logic component to be linked to GUI component by same ID name
     */
    private void createComponent(Point2D component)
    {
        Button obj = new Button(component.toString());
        Text stationID = new Text();
        obj.setId(component.toString());
        obj.setText("");
        setOfComp.add(obj);

        if (component instanceof Light || component instanceof Switch)
        {
            obj.setLayoutX(component.getX() * width + 0.25 * width);
            obj.setLayoutY(component.getY() * height + 0.25 * height);
            obj.setText("");
            if (component instanceof Light)
            {
                obj.setPrefSize(width/4, width/4);
                obj.setRotate(-90);
                obj.setShape(new Circle(width));
            }
            else
            {
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(50.0, 0.0,  0.0, 50.0, 100.0, 50.0);
                obj.setPrefSize(width / 2, height / 2);
                obj.setShape(triangle);
            }
        }
        else
        {
            obj.setPrefSize(width, height);
            obj.setLayoutX(component.getX() * width);
            obj.setLayoutY(component.getY() * height);

            if(component instanceof Track)
            {
                if(((Track) component).getTopSwitch() != null && ((Track)component).getBottomSwitch() != null)
                {   //diagonal track, will set the image differently
                    obj.setStyle("-fx-background-color: red");
                    obj.setText("");
                    obj.setBackground(new Background(new BackgroundImage(
                            new Image("file:resources/rail.png"),
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(width+70, height, false, false, false, false)
                    )));

                    obj.setPrefSize(width+70, height);
                    obj.setTranslateX(-35);
                    if(((Track) component).getTopSwitch().getX() < ((Track)component).getBottomSwitch().getX())
                        obj.setRotate(-25);
                    else obj.setRotate(25);

                }
                else    //regular horizontal track
                {
                    obj.setText("");
                    obj.setBackground(new Background(new BackgroundImage(
                            new Image("file:resources/rail.png"),
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(width, height, false, false, false, false)
                    )));
                }
            } else if(component instanceof Station)
            {
                stationID.setText(Integer.toString(((Station) component).getStationNumber()));
                stationID.setLayoutX(component.getX()*width+40);
                stationID.setLayoutY(component.getY()*height+25);
                stationID.setStyle("-fx-font-size:32;" + "-fx-fill: gold;" + "-fx-stroke: black");
                if(((Station) component).getStationNumber() > 9)
                    stationID.setLayoutX((component.getX()*width)+30);

                obj.setBackground(new Background(new BackgroundImage(
                        new Image("file:resources/station.png"),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(width, height, false, false, false,false)
                )));
            }
        }
        getChildren().add(obj);
        getChildren().add(stationID);
    }

    /**
     * promptForTrainPlacement:
     * when the game(?) is first launched, this method is called
     * asking the user how many trains they would like to place.
     * Once the user has entered how many trains, prompts for each
     * train are then called
     */
    public void promptForTrainPlacement()
    {
        if(!dialogBoxIsUp)
        {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);


            HBox holderOfVBoxes = new HBox(5);
            VBox howManyTrains = new VBox(5);

            Label prompt = new Label("Enter the amount of trains you want:");
            TextField totalAmountOfTrains = new TextField();
            totalAmountOfTrains.setPrefSize(15, 30);

            totalAmountOfTrains.setOnKeyPressed(event ->
            {
                if(event.getCode() == KeyCode.ENTER)
                {
                    int numOfTrainsPrompt = Integer.parseInt(totalAmountOfTrains.getText());
                    if(numOfTrainsPrompt < totalStations())
                    {
                        if(numOfTrainsPrompt > 0)
                        {
                            launchPromptsForTrainPlacement(numOfTrainsPrompt);
                        }
                        dialog.close();
                        dialogBoxIsUp = false;
                    }
                    else
                    {
                        prompt.setText("Enter a train number below the total stations " + totalStations());
                    }
                }
            });

            howManyTrains.getChildren().addAll(prompt, totalAmountOfTrains);
            holderOfVBoxes.getChildren().addAll(howManyTrains);

            Scene dialogScene = new Scene(holderOfVBoxes, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
            dialogBoxIsUp = true;
        }
    }

    /**
     * launchPromptsForTrainPlacement: launch a prompt
     * for each train out of the trains the user wanted. For each train,
     * the user is asked for the station where they would like to place it
     * in the start of the game(?). If it's a valid station, the Train button/image
     * is created and placed at the track next to it's docked station.
     *
     * @param numOfPrompts the number of trains the user entered to place will be the number of our prompts
     */
    private void launchPromptsForTrainPlacement(int numOfPrompts)
    {
        for(int i =0; i < numOfPrompts; i++)
        {
            Stage promptForTrain = new Stage();
            promptForTrain.initModality(Modality.APPLICATION_MODAL);
            VBox dialogVbox = new VBox(5);

            Label promptPlaceTrain = new Label("Enter the station number where you\nwould like to place train " + (i+1));
            TextField trainPlacement = new TextField();
            trainPlacement.setPrefSize(15, 30);

            trainPlacement.setOnKeyPressed(event2 ->
            {
                if (event2.getCode() == KeyCode.ENTER)
                {
                    dockedStation = trainPlacement.getText();

                    boolean isNumericUno = dockedStation.chars().allMatch( Character::isDigit );

                    if(isNumericUno)
                    {
                        placeTrainAtStation.accept(dockedStation);

                        if(validStation)
                        {
                            Train currentTrain = trains.get(this.numOfTrains);   //get the most recent train added for current train

                            setOfLogicComp.add(currentTrain);

                            Button obj = new Button(currentTrain.toString());
                            obj.setPrefSize(width / 2 + 5, height / 2 + 5);
                            obj.setLayoutX(currentTrain.getTrackCurrentlyOn().getX() * width + 0.25 * width);
                            obj.setLayoutY(currentTrain.getTrackCurrentlyOn().getY() * height + 0.25 * height);

                            currentTrain.setTrainGuiX(currentTrain.getTrackCurrentlyOn().getX() * width + 0.25 * width);
                            currentTrain.setTrainGuiY(currentTrain.getTrackCurrentlyOn().getY() * height + 0.25 * height);

                            obj.setText("");

                            setOfComp.add(obj);

                            obj.setOnAction(new EventHandler<ActionEvent>()
                            {
                                @Override
                                public void handle(ActionEvent event)
                                {
                                    currentTrainSetter.accept(obj.getId());
                                    launchDestinationPrompt();
                                }
                            });

                            obj.setId(currentTrain.toString());
                            drawTrain(obj, currentTrain);
                            getChildren().add(obj);
                            promptForTrain.close();
                        }
                    }
                }
            });

            dialogVbox.getChildren().addAll(promptPlaceTrain, trainPlacement);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            promptForTrain.setScene(dialogScene);
            promptForTrain.show();
        }
    }

    /**
     * launchDestinationPrompt:
     * Once a train has been clicked on with a mouse, launchDestinationPrompt
     * is called. A simple sub-pane is launched asking the user to enter which station the user
     * would like the train to go to. If it is a valid station destination, findRoute is called from the Coontroller
     * If a route is not found, a label is created to let you know so.
     */
    private void launchDestinationPrompt()
    {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(5);

        Label routeNotPossible = new Label();
        Label promptDesti = new Label("Enter the station number (above the station) you \nwould like to go to, press enter once done");
        TextField trainDestination = new TextField();
        trainDestination.setPrefSize(15, 30);
        trainDestination.setOnKeyPressed(event ->
        {
            if (event.getCode() == KeyCode.ENTER)
            {
                destination = trainDestination.getText();
                boolean isNumeric = destination.chars().allMatch( Character::isDigit );

                if(validStation)
                {
                    if (isNumeric)
                    {
                        findRoute.accept(destination);
                        if (stationDoesntExist)
                        {
                            routeNotPossible.setText("That station doesn't exist. Please enter valid station.");
                            stationDoesntExist = false;
                        } else if (routeFound != null)
                        {
                            dialogBoxIsUp = false;
                            stationDoesntExist = false;
                            routeFound = null;
                            dialog.close();
                        } else
                        {
                            routeNotPossible.setText("Route not possible to that station");
                        }
                    } else
                    {
                        routeNotPossible.setText("Please enter a numeric value.");
                    }
                }
            }
        });

        dialogVbox.getChildren().addAll(promptDesti, trainDestination, routeNotPossible);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
        dialogBoxIsUp = true;
    }

    /**
     * tellsTrainsToGoListener:
     * adds listener to the GUI for ENTER and the S key. If ENTER is toggled
     * trains will go right when they get a route. If S is toggled, trains
     * will not go until they have a route.
     * trainGoState is a label letting the user know what is currently toggled.
     */
    private void tellTrainsToGoListener()
    {
        trainGoState = new Label("S Toggled: Trains waiting for enter to go");
        trainGoState.setStyle("-fx-font-size:18");
        getChildren().add(trainGoState);

        this.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER)
            {
                trainsLaunch = true;
                trainGoState.setText("ENTER Toggled: Trains will go once route given");
            }

            if(event.getCode() == KeyCode.S)
            {
                trainsLaunch = false;
                trainGoState.setText("S Toggled: Trains waiting for enter to go");
            }
        });
    }

    private int totalStations()
    {
        int totalStations = 0;

        for(Point2D step : setOfLogicComp)
        {
            if(step.toString().startsWith("Station"))
                totalStations++;
        }
        return totalStations;
    }

    public void setRails(List<List<Point2D>> rails)
    {
        this.rails = rails;
    }
    public void setCounter(int counter)
    {
        this.counter = counter;
    }
    public void setRouteFound(List<Track> routeFound)
    {
        this.routeFound = routeFound;
    }
    public void setStationDoesntExist(boolean expr) { stationDoesntExist = expr; }
    public void setValidStation(boolean expr) { validStation = expr; }
    public void setTrains(ArrayList<Train> trains) { this.trains = trains; numOfTrains = trains.size()-1; }
    public void setFindRoute(Consumer<String> findRoute) { this.findRoute = findRoute; }
    public void setPlaceTrainAtStation(Consumer<String> placeTrain) { placeTrainAtStation = placeTrain; }
    public void setCurrentTrain(Consumer<String> currTrain) { currentTrainSetter = currTrain; }
}
