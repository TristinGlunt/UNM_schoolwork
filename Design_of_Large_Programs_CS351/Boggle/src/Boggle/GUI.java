package Boggle;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 * The code GUI is our coordinator of event based handling for the Boggle game.
 * It has our handlers for the buttons other than the gameboard and rotate board,
 * and mainly draws all of our displays. Launches the logical game coordinator giving
 * the type of board user selects to play on.
 * @author Tristin Glunt | tglunt@unm.edu | CS 351 Project 2 Boggle
 *
 */
public class GUI
{
    private Pane root = new Pane();     //main pane of GUI to display board
    private GridPane gbd;               //gbd is the display of the game board
    private Pane goodWordList;          //accepted words entered by player
    private Stage primaryStage;         //stage passed down from JavaFX thread
    private Scene startScene;           //the start scene of the gui
    private Label scoreLabel;           //displays current game score
    private Label wordListTitle;        //Title of words entered

    private Coordinator coord;          //checks words and coordinates things the GUI doesn't need to know about

    /**
     * GUI:
     * The GUI handles all of our display and interations with the User. It creates
     * our different scenes, and launches our Coordinator with the correct Board to handle
     * our game logic
     * @param primaryStage the stage passed down from the JavaFX thread
     */
    public GUI(Stage primaryStage)
    {
        this.primaryStage = primaryStage;

        wordListTitle = new Label("Word List");
        wordListTitle.setStyle("-fx-font-size: 24");

        initialGUIElements();
        createStartScene();                                 //when the GUI is launched we will create the start display
    }

    /**
     * initialGUIElements: loads the initial GUI elements for the playStage, also sets the root to have
     * the enterListener so the user doesn't have to specifically click on anything to hit enter, can always
     * just hit enter whenever playing game
     */
    private void initialGUIElements()
    {
        goodWordList = new Pane();
        goodWordList.setLayoutX(1000);
        goodWordList.setLayoutY(100);
        goodWordList.getChildren().add(wordListTitle);

        scoreLabel = new Label();
        scoreLabel.setLayoutX(50);
        scoreLabel.setLayoutY(150);
        scoreLabel.setStyle("-fx-font-size: 32");

        root.getChildren().add(goodWordList);
        root.getChildren().add(scoreLabel);
        root.setOnKeyPressed(enterListener);             //listener for enter to enter current word guess, added to
                                                         //root so you can hit enter whenever
    }

    /**
     * addGameBoardDisplay: gets the gameboard display from the coordinator and adds it
     * to the root pane which contains all of our displays
     */
    private void addGameBoardDisplay()
    {
        gbd = coord.getGameBoardDisplay();
        root.getChildren().add(gbd);
    }

    /**
     * addRotateButtons: gets the rotate buttons from the coordinator and adds it to
     * the root pane
     */
    private void addRotateButtons()
    {
        Button rotateLeft = coord.getRotateLeftButton();
        Button rotateRight = coord.getRotateRightButton();
        root.getChildren().add(rotateLeft);
        root.getChildren().add(rotateRight);
    }

    /**
     * addRotateButtons: gets the clock display from the coordinator and adds it to
     * the root pane
     */
    private void addClockDisplay()
    {
        Pane clockDisplay = coord.getClockDisplay();
        root.getChildren().add(clockDisplay);
    }

    /**
     * createStartScene: creates the start display for the GUI, it displays our buttons
     * which set the board to play, the rules button, and our animated spinning board and title boggle
     */
    private void createStartScene()
    {
        Pane startPane = new Pane();
        Button fourbyfour = new Button("4x4 Boggle");
        Button fivebyfive = new Button("5x5 Boggle");
        Button rulesBtn = new Button("Rules");


        fourbyfour.setStyle("-fx-font-size: 24");
        fivebyfive.setStyle("-fx-font-size: 24");
        rulesBtn.setStyle("-fx-font-size: 24");

        fourbyfour.setOnAction(fourByFour);
        fivebyfive.setOnAction(fiveByFive);
        rulesBtn.setOnAction(rules);

        fourbyfour.setLayoutY(200);
        fourbyfour.setLayoutX(760);

        fivebyfive.setLayoutX(760);
        fivebyfive.setLayoutY(270);

        rulesBtn.setLayoutX(785);
        rulesBtn.setLayoutY(340);

        startPane.getChildren().add(fourbyfour);
        startPane.getChildren().add(fivebyfive);
        startPane.getChildren().add(rulesBtn);

        Image boggleTitle = new Image("file:resources/boggletitle.png", false);
        ImageView boggleIMGV = new ImageView(boggleTitle);
        boggleIMGV.setLayoutX(705);
        boggleIMGV.setLayoutY(100);
        startPane.getChildren().add(boggleIMGV);

        GameBoard visualGameBoard = new GameBoard(4, null);   //make temp board for
        GridPane visualGameBoardDisplay = visualGameBoard.getGameBoardDisplay();                 //visual display
        visualGameBoardDisplay.setLayoutY(100);
        visualGameBoardDisplay.setLayoutX(100);
        visualGameBoardDisplay.setMaxSize(100, 100);
        RotateTransition rt = new RotateTransition(Duration.millis(5000), visualGameBoardDisplay);

        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.play();

        startPane.getChildren().add(visualGameBoardDisplay);

        startScene = new Scene(startPane, 1200, 1000);
        primaryStage.setScene(startScene);                             //passed down by main so GUI can change the scene
    }

    /**
     * createRulesPane: simply creates a scene for our rules that is a giant string, and has a button to navigate
     * back to the menu
     */
    private Pane createRulesPane()
    {
        Pane rulesPane = new Pane();
        Button menu = new Button("<- Menu");
        menu.setOnAction(event -> {
            primaryStage.setScene(startScene);
        });
        menu.setLayoutY(200);
        menu.setLayoutX(200);
        Label rules = new Label("The game is usually played with 4x4 or 5x5 \n" +
                "   tray that holds dice, which display letters on all sides.\n" +
                "The game starts by shaking the dice until they fall until place.\n" +
                "A three-minute timer is started.\n" +
                "The game is over when the timer expires.\n" +
                "The player gets points only for words that: \n" +
                "   are visible on the tray\n" +
                "   are listed in the dictionary\n" +
                "   are not capitalized or foreign words\n" +
                "   do not appear on any other player's list\n" +
                "   are at least three-letters long\n" +
                "There is no penalty for rejected words.\n" +
                "Distinct words can use the same dice in the bard, e.g. 'home', 'homes', and 'homed',\n" +
                "   might all appear in the board.\n" +
                "The number of points received for a given word equals the length of the word\n" +
                "   minues two, e.g., 1 point for 'dog', 2 points for 'lava' and 4 points for 'retina'.\n" +
                "A word appears in the board if it consists of a string of letters that is formed\n" +
                "   by traversing neighboring dice without stepping on the same dice twice.");
        rules.setLayoutX(400);
        rules.setLayoutY(200);
        rulesPane.getChildren().add(rules);
        rulesPane.getChildren().add(menu);
        return rulesPane;
    }

    /**
     * create the gameOver display for when the timer has run out
     * @param totalPoints totalpoints gained from correctly entered words
     */
    private void gameOverDisplay(int totalPoints)
    {
        Label tp = new Label("Your score: " + Integer.toString(totalPoints));
        tp.setLayoutX(684);
        tp.setLayoutY(600);
        tp.setStyle("-fx-font-size: 48");
        Label possibleWords = new Label(coord.possibleWords());
        ScrollPane sp = new ScrollPane();
        sp.setContent(possibleWords);
        sp.setMaxSize(500, 400);
        Label titleOfPossible = new Label("Possible Words:");
        titleOfPossible.setStyle("-fx-font-size: 32");
        titleOfPossible.setLayoutY(100);
        titleOfPossible.setLayoutX(684);
        sp.setStyle("-fx-border-color: transparent");
        sp.setLayoutX(684);
        sp.setLayoutY(150);

        Pane popup = new Pane();
        popup.getChildren().add(tp);
        popup.getChildren().add(titleOfPossible);
        popup.getChildren().add(sp);
        gbd.setLayoutX(5);
        gbd.setLayoutY(100);
        coord.resetGameBoardAfterEnter();                               //once the game is over reset highlighted words
        popup.getChildren().add(gbd);

        Button playAgain = new Button("Play Again");
        playAgain.setStyle("-fx-font-size: 24");
        playAgain.setOnAction(event -> {                               //tell the coordinator to restart the game as well
            coord.restartGame();                                       //as resetting the GUI elements
            resetGUI();
        });

        playAgain.setLayoutY(300);
        playAgain.setLayoutX(535);
        popup.getChildren().add(playAgain);

        Scene popUpStage = new Scene(popup, 1200, 1000);

        primaryStage.setScene(popUpStage);


        Path topScoreFile = Paths.get("resources/topScores.txt");           //current writes your score to a text file
        List<String> lines = Arrays.asList(Integer.toString(totalPoints));      //would want a tag (like your name)
        try                                                                     //to be added with it
        {
            Files.write(topScoreFile, (Integer.toString(totalPoints) + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e)
        {
            System.err.println("File not found");
        }
    }

    /**
     * fourByFour: event handler for the 4x4 Board button on the start screen. Activating this handler
     * launches a new Coordinator and changes the scene to be that of the gameboard display with a 4x4 Boggle board
     */
    private EventHandler<javafx.event.ActionEvent> fourByFour = new EventHandler<javafx.event.ActionEvent>()
    {
        @Override
        public void handle(javafx.event.ActionEvent event)
        {
            coord = new Coordinator(startScene, 4, primaryStage, j -> gameOverDisplay(j));
            coord.resetGuessedWord();   //incase the user was clicking the spinning board at start
            addGameBoardDisplay();      //adds the gameBoardDisplay after the coordinator creates the board
            addRotateButtons();
            addClockDisplay();

            Scene playScene = new Scene(root, 1200, 1000);
            primaryStage.setScene(playScene);
        }
    };

    /**
     * fiveByFive: event handler for the 5x5 Board button on the start screen. Activating this handler
     * launches a new Coordinator and changes the scene to be that of the gameboard display with a 5x5 Boggle board
     */
    private EventHandler<javafx.event.ActionEvent> fiveByFive = new EventHandler<javafx.event.ActionEvent>()
    {
        @Override
        public void handle(javafx.event.ActionEvent event)
        {
            coord = new Coordinator(startScene, 5, primaryStage, j -> gameOverDisplay(j));
            coord.resetGuessedWord();
            addGameBoardDisplay();      //adds the gameBoardDisplay after the coordinator creates the board
            addRotateButtons();
            addClockDisplay();

            Scene playScene = new Scene(root, 1200, 1000);
            primaryStage.setScene(playScene);
        }
    };

    /**
     * rules: handler for the rules button of the start scene, launches the rules scene
     */
    private EventHandler<javafx.event.ActionEvent> rules = new EventHandler<javafx.event.ActionEvent>()
    {
        @Override
        public void handle(javafx.event.ActionEvent event)
        {
            Pane rulesPane = createRulesPane();


            Scene rulesScene = new Scene(rulesPane, 1200, 1000);
            primaryStage.setScene(rulesScene);
        }
    };

    /**
     * enterListener: enterListener takes the currentlyGuessed word that gets built up from the user
     * clicking on the different possible characters in a board and calls helper methods to handle an
     * entered word. It also updates the display of accepted/denied words entered, and lastly resets the
     * boards highlighted
     */
    private EventHandler<KeyEvent> enterListener = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if(event.getCode() == KeyCode.ENTER)
            {
                coord.enterGuessedWord();
                coord.resetGuessedWord();
                updateGuiBadWords();
                updateGuiGoodWords();
                coord.resetGameBoardAfterEnter();
            }
        }
    };

    /**
     * updateGuiGoodWords: updates the display of accepted words on the game board display part of the gameboard.
     * It does this by looping through the coordinators accepted words and draws over them.
     */
    private void updateGuiGoodWords()
    {
        int yLayoutOfWords1 = 30;
        for(String step : coord.getCorrectlyGuessedWords())
        {
            Label tempWord = new Label(step);
            scoreLabel.setText("Current score: " + coord.getTotalPoints());
            tempWord.setStyle("-fx-text-fill: green; -fx-font-size: 16");
            tempWord.setLayoutY(yLayoutOfWords1);
            goodWordList.getChildren().add(tempWord);

            yLayoutOfWords1 += 15;
        }
    }

    /**
     * updateGuiBadWords: updates the display of denied words on the gameboard display part of the gameboard.
     * It does this by looping through the coordinators denied words and draws over them.
     */
    private void updateGuiBadWords()
    {
        int yLayoutOfWords2 = 30;
        for(String step : coord.getIncorrectlyGuessedWords())
        {
            Label tempWord = new Label(step);
            tempWord.setStyle("-fx-text-fill: red; -fx-font-size: 16");
            tempWord.setLayoutX(55);
            tempWord.setLayoutY(yLayoutOfWords2);
            goodWordList.getChildren().add(tempWord);

            yLayoutOfWords2 += 15;
        }
    }

    /**
     * resetGUI: resets the GUI elements, re-initializes initial GUI elements. called when restarting a game
     */
    private void resetGUI()
    {
        goodWordList = null;
        scoreLabel = null;
        root = null;
        root = new Pane();
        coord.resetGuessedWord();
        initialGUIElements();

    }
}
