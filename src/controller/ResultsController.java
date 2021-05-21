package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Result;
import java.util.List;

public class ResultsController {

  private List<Result> results;

  @FXML
  private VBox resVBox;



  public void sendResults(List<Result> results) {

    for (Result r : results){

      System.out.println(r.getTitle() + " " + r.getFocus());
      resVBox.getChildren().add(new Text(r.getTitle()));
    }
  }
}
