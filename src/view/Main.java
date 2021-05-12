package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Indexer;
import model.LuceneConstants;
import model.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    private static Main instance;
    private Searcher searcher;

    public Main() throws IOException {
        Indexer indexer = new Indexer(LuceneConstants.INDEX_PATH, LuceneConstants.ARTICLES_PATH);
        searcher = new Searcher((LuceneConstants.INDEX_PATH));
    }

    @Override
    public void start(Stage primaryStage) throws IOException, ParseException {

        Parent root = FXMLLoader.load(getClass().getResource("landing-page.fxml"));
        primaryStage.setTitle(LuceneConstants.APP_NAME);
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException, ParseException {
        Main m = getInstance();
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
