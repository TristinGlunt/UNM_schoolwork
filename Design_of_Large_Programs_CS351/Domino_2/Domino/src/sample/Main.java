package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends Application
{
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGTH = 725;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        //Boneyard boneyard = new Boneyard();
        //BoardGUI gui = new BoardGUI();
        //gui.buildBoard();
        Coordinator referee = new Coordinator();
        Pane  pane = new Pane();
        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGTH);


        stage.setScene(scene);
        stage.setTitle("Domino");
        //stage.show();
    }
}
