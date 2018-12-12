package Boggle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
    @author: Tristin Glunt | tglunt@unm.edu
    CS 351 - Project 2 - Boggle
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        GUI gui = new GUI(primaryStage);                    //create the main GUI object, pass down the main stage of
        primaryStage.setTitle("Boggle");                    //javafx so the GUI can change the scene when needed
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
