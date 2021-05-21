package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import view.Main;
import java.io.IOException;
import java.util.List;

public class LandingPageController {

    @FXML
    private TextField searchInput;

    @FXML
    public void searchDocuments(ActionEvent event) throws IOException, ParseException {

        String toSearch = searchInput.getText();
        List<Result> results
            = Main.getInstance().getSearcher().search(toSearch);

        showResultScene(event, results);
    }

    private void showResultScene(ActionEvent event, List<Result> results) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/results-page.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));

        ResultsController controller = loader.getController();
        controller.sendResults(results);
        stage.show();
    }
}
