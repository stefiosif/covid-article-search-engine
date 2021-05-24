package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

  public void initialize() {

    historyVBox.setAlignment(Pos.TOP_CENTER);
    btnPrev.setVisible(false);
    history = new LinkedList<>();
  }

  private void search(String query) throws IOException, ParseException {

    List<Result> results
        = Main.getInstance().getSearcher().search(query);

    updateHistory(query);
    showResults(results, 0);
  }

  public void showResults(List<Result> res, int page) {

    setResults(res);
    resVBox.getChildren().clear();

    if (res.size() == 0) {

      Text notFound = new Text("No results found.");
      notFound.setFont(new Font("Candara Light", 17.0));
      resVBox.getChildren().add(notFound);
      return;
    }

    int x = 10;
    if (results.size() - (10 * pageId) < 10)
      x = res.size() - page * 10;
    for (int i = page * 10; i < page * 10 + x; i++) {

      Hyperlink link = new Hyperlink(res.get(i).getTitle());
      Text desc = new Text(res.get(i).getHighlight());
      desc.setWrappingWidth(600);
      link.setWrapText(true);
      link.setFont(new Font("Candara Light", 17.0));
      setLink(link);
      desc.setFont(new Font("Candara Light", 12.0));
      resVBox.getChildren().add(link);
      resVBox.getChildren().add(desc);
    }
  }

  private void setLink(Hyperlink link) {

    link.setOnAction(e -> {
      String txt = link.getText();
      for (Result r : results) {
        if (r.getTitle().equals(txt)) {
          viewDoc(r);
          break;
        }
      }
    });
  }

  private void viewDoc(Result r) {

    ScrollPane pane = new ScrollPane();
    pane.setPadding(new Insets(10));
    VBox vbox = new VBox();
    pane.setContent(vbox);

    Stage stage = new Stage(StageStyle.UTILITY);
    stage.setScene(new Scene(pane, 800, 600));
    stage.setTitle(r.getTitle());

    Text title = new Text(r.getTitle());
    title.setWrappingWidth(780);
    title.setFont(new Font("Arial", 18.0));
    title.setUnderline(true);

    Text contents = new Text(r.getContents());
    contents.setWrappingWidth(780);
    contents.setFont(new Font("Arial", 14.0));

    vbox.getChildren().add(title);
    vbox.getChildren().add(new Text("\n"));
    vbox.getChildren().add(contents);

    stage.show();
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
