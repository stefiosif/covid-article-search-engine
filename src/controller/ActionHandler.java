package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ActionHandler {

    @FXML
    private TextField searchInput;

    @FXML
    void searchDocuments(ActionEvent event) {
        String toSearch = searchInput.getText();
        System.out.println(toSearch);
    }

}
