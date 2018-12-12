import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * =========================================
 *
 * SmartRail - CS 351 Project 3
 * @author Tristin Glunt | tglunt@unm.edu
 * @author Duong Nguyen | dnguyen@unm.edu
 * 11/16/17
 * =========================================
 */

public class Main extends Application {

    private Object syncObject = new Object();   //global variable that handles waits and notifies
    public static int globalVariable = 1;

    /**
     * start the javafx application. loads our viewmanager (GUI) and our
     * our controller which handles the logic
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ViewManager viewManager = new ViewManager(syncObject);
        Scene scene = new Scene(viewManager);

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("file:resources/toytrain.png"));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e ->
        {
            System.exit(0);
        });

        Controller controller = new Controller(viewManager, syncObject);
        controller.start();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
