package com.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.model.Indexer;
import com.model.Constants;
import com.model.Searcher;

import java.io.IOException;

public class Main extends Application {

    private static Main instance;
    private final Searcher searcher = null;
    private final Indexer indexer;

    public Main() throws IOException {

        indexer = new Indexer(Constants.INDEX_PATH, Constants.ARTICLES_PATH);
        indexer.index();
        //searcher = new Searcher(Constants.INDEX_PATH);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/landing-page.fxml"));
        Parent root = loader.load();
        stage.setTitle(Constants.APP_NAME);
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void main(String[] args) throws IOException {

        // Create Singleton Main instance
        getInstance();

        // Run JavaFX Application
        launch(args);
    }

    public Searcher getSearcher(){
        return searcher;
    }

    public Indexer getIndexer(){
        return indexer;
    }

    public static Main getInstance() throws IOException {

        if (instance == null){
            instance = new Main();
        }
        return instance;
    }
}
