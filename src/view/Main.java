package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Indexer;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException{

        Parent root = FXMLLoader.load(getClass().getResource("app-gui.fxml"));
        primaryStage.setTitle("Covid19 Search Engine");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
        Indexer ind = new Indexer("tmp/index");

    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
