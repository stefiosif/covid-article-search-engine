package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Indexer;
import model.Constants;
import model.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;
import java.io.IOException;

public class Main extends Application{

    private static Main instance;
    private Searcher searcher;

    public Main() throws IOException {
        Indexer indexer = new Indexer(Constants.INDEX_PATH, Constants.ARTICLES_PATH);
        indexer.index();
        searcher = new Searcher((Constants.INDEX_PATH));
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("landing-page.fxml"));
        stage.setTitle(Constants.APP_NAME);
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void main(String[] args) throws IOException, ParseException {

        // Create Singleton Main instance
        getInstance();

        // Run JavaFX Application
        launch(args);
    }

    public Searcher getSearcher(){

        return searcher;
    }

    public static Main getInstance() throws IOException {

        if (instance == null){
            instance = new Main();
        }
        return instance;
    }
}
