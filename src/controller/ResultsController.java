package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import view.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ResultsController {

  @FXML
  private VBox resVBox, historyVBox;

  @FXML
  private TextField searchInput;

  @FXML
  private Button btnPrev, btnNext;

  private int pageId = 0;
  private List<Result> results;
  private LinkedList<String> history;

  @FXML
  public void searchAdvanced(ActionEvent event) throws IOException, ParseException {

    String query = searchInput.getText();
    search(query);
  }

  private void search(String query) throws IOException, ParseException {

    List<Result> results
        = Main.getInstance().getSearcher().search(query);

    updateHistory(query);
    showResults(results, 0);
  }

  public void initialize() {

    historyVBox.setAlignment(Pos.TOP_CENTER);
    btnPrev.setVisible(false);
    history = new LinkedList<>();
  }

  public void showResults(List<Result> res, int page) {

    setResults(res);
    resVBox.getChildren().clear();
    int x = 10;
    if (results.size() - (10 * pageId) < 10)
      x = res.size() - page * 10;
    for (int i = page * 10; i < page * 10 + x; i++) {

      Hyperlink link = new Hyperlink(res.get(i).getTitle());
      Text desc = new Text(res.get(i).getHighlight());
      desc.setWrappingWidth(600);
      link.setWrapText(true);
      link.setFont(new Font("Candara Light", 17.0));
      desc.setFont(new Font("Candara Light", 12.0));
      resVBox.getChildren().add(link);
      resVBox.getChildren().add(desc);
    }
  }

  private void setResults(List<Result> results) {
    this.results = results;
  }

  public void showPrevious(ActionEvent event) {

    resVBox.getChildren().clear();
    showResults(results, --pageId);

    btnNext.setVisible(true);
    if (pageId == 0)
      btnPrev.setVisible(false);
  }

  public void showNext(ActionEvent event) {

    resVBox.getChildren().clear();
    showResults(results, ++pageId);

    btnPrev.setVisible(true);
    if (results.size() - (10 * pageId) < 10)
      btnNext.setVisible(false);
  }

  public void updateHistory(String query) {

    if (history.contains(query))
      return;

    history.addFirst(query);
    if (history.size() > 10) {
      history.removeLast();
    }

    historyVBox.getChildren().clear();
    for (String q : history) {
      Hyperlink link = new Hyperlink(q);
      link.setWrapText(true);
      link.setFont(new Font("Candara Light", 13.0));

      link.setOnAction(e -> {
        try {
          search(link.getText());
        } catch (IOException | ParseException ioException) {
          ioException.printStackTrace();
        }
      });

      historyVBox.getChildren().add(link);
    }
  }
}
