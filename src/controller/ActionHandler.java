package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.lucene.queryparser.classic.ParseException;
import view.Main;
import java.io.IOException;
import java.util.ArrayList;

public class ActionHandler {

    @FXML
    private TextField searchInput;
    @FXML
    private VBox vBox;

    @FXML
    public void searchDocuments(ActionEvent event) throws IOException, ParseException {
        String toSearch = searchInput.getText();
        Main m = Main.getInstance();
        ArrayList<ArrayList<String>> results = m.getSearcher().search(toSearch);

        Parent root = FXMLLoader.load(getClass().getResource("/view/results-page.fxml"));
        Scene resultScene = new Scene(root);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();


        for (ArrayList<String> field : results){
            for (String str : field){
                vBox.getChildren().add(new Text(str));
            }
        }

        window.setScene(resultScene);
        window.show();

    }
}
