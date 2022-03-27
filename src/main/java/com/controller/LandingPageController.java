package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import java.io.IOException;

public class LandingPageController {

    @FXML
    private TextField searchInput;

    @FXML
    public void searchSimple(ActionEvent event) throws IOException, ParseException, InvalidTokenOffsetsException {

        String query = searchInput.getText();
        if (query == null || query.equals("")) return;

        // Load new stage from fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/results-page.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));

        ResultsController controller = loader.getController();
        controller.search(query);

        stage.show();
    }

}
