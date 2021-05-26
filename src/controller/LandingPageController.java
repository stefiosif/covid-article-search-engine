package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.NextQuery;
import model.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import view.Main;
import java.io.IOException;
import java.util.List;

public class LandingPageController {

    @FXML
    private TextField searchInput;

    @FXML
    public void searchSimple(ActionEvent event) throws IOException, ParseException, InvalidTokenOffsetsException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/results-page.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));

        // Pass results to results-page.fxml controller class
        ResultsController controller = loader.getController();

        controller.updateHistory(searchInput.getText());
        controller.search(searchInput.getText());

        stage.show();

    }

}
