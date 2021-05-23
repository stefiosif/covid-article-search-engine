package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import view.Main;

import java.io.IOException;
import java.util.List;

public class ResultsController {

  @FXML
  private VBox resVBox;

  @FXML
  private TextField searchInput;

  @FXML
  public void searchAdvanced(ActionEvent event) throws IOException, ParseException {

    String toSearch = searchInput.getText();
    List<Result> results
        = Main.getInstance().getSearcher().search(toSearch);

    resVBox.getChildren().clear();
    showResults(results);
  }

  public void showResults(List<Result> results) {

    for (Result r : results){

      Hyperlink link = new Hyperlink(r.getTitle());
      Text desc = new Text(r.getTitle());
      desc.setWrappingWidth(600);
      link.setWrapText(true);
      link.setFont(new Font("Candara Light", 17.0));
      desc.setFont(new Font("Candara Light", 12.0));
      resVBox.getChildren().add(link);
      resVBox.getChildren().add(desc);
    }
  }

  public void showPrevious(ActionEvent event) {

  }

  public void showNext(ActionEvent event) {

  }

}
