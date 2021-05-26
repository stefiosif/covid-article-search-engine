package controller;

import com.sun.webkit.WebPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.NextQuery;
import model.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import view.ArticleViewer;
import view.Main;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ResultsController {

  @FXML
  private VBox resVBox;

  @FXML
  private TextField searchInput;

  @FXML
  private Button btnPrev, btnNext;

  @FXML
  private Text metaText;

  @FXML
  private HBox suggestHBox;

  @FXML
  private ComboBox<String> sortBox, focusBox;

  private int pageId = 0;
  private List<Result> results;
  private LinkedList<String> history;
  private NextQuery nextQuery;

  public void initialize() {

    // Create Advanced search options
    ObservableList<String> sortOptions = FXCollections.observableArrayList(
        "relevance", "date");
    sortBox.setItems(sortOptions);
    ObservableList<String> focusOptions = FXCollections.observableArrayList(
        "business", "general", "tech", "consumer", "science", "finance");
    focusBox.setItems(focusOptions);

    // Store history of queries to use in further searches
    history = new LinkedList<>();

    // Create nextQuery
    nextQuery = new NextQuery();
  }

  @FXML
  public void searchAdvanced(ActionEvent event) throws IOException, ParseException, InvalidTokenOffsetsException {

    String query = searchInput.getText();
    search(query);
  }

  @FXML
  public void showPrevious(ActionEvent event) throws IOException {

    resVBox.getChildren().clear();
    showResults(results, --pageId);

    btnNext.setVisible(true);
    if (pageId == 0)
      btnPrev.setVisible(false);
  }

  @FXML
  public void showNext(ActionEvent event) throws IOException {

    resVBox.getChildren().clear();
    showResults(results, ++pageId);

    btnPrev.setVisible(true);
    if (results.size() - (6 * pageId) < 6)
      btnNext.setVisible(false);
  }

  @FXML
  void sortBy(ActionEvent event) {
    nextQuery.setSort(sortBox.getSelectionModel().getSelectedItem());
  }

  @FXML
  void chooseFocus(ActionEvent event) {
    nextQuery.setFocus(focusBox.getSelectionModel().getSelectedItem());
  }

  public void search(String query) throws IOException, ParseException, InvalidTokenOffsetsException {

    long start = System.nanoTime();

    List<Result> results
        = Main.getInstance().getSearcher().search(query, nextQuery);

    updateHistory(query);
    showResults(results, 0);

    // Set suggestions
    String[] suggestions = Main.getInstance().getIndexer().getSpellChecker().suggestSimilar(query, 3);
    suggestHBox.getChildren().clear();
    for (var s : suggestions) {
      Hyperlink link = new Hyperlink(s);
      link.setOnAction(e -> {
        String txt = link.getText();
        try {
          search(txt);
        } catch (IOException ioException) {
          ioException.printStackTrace();
        } catch (ParseException parseException) {
          parseException.printStackTrace();
        } catch (InvalidTokenOffsetsException invalidTokenOffsetsException) {
          invalidTokenOffsetsException.printStackTrace();
        }
      });
      suggestHBox.getChildren().add(link);
    }

    Font openSans14 = Font.loadFont(ResultsController.class.getResource(
        "/view/OpenSans-Regular.ttf").toExternalForm(), 14);
    var elapsedTime = System.nanoTime() - start;
    double searchingTime = (double) elapsedTime / 1_000_000_000;
    metaText.setFont(openSans14);
    metaText.setText(results.size() + " search results (" + searchingTime + " seconds)");

  }

  public void showResults(List<Result> res, int page) throws IOException {

    this.results = res;

    // Show search results
    resVBox.getChildren().clear();

    Font openSans18 = Font.loadFont(ResultsController.class.getResource(
        "/view/OpenSans-Regular.ttf").toExternalForm(), 17);
    Font openSans14 = Font.loadFont(ResultsController.class.getResource(
        "/view/OpenSans-Regular.ttf").toExternalForm(), 13);

    // Check if there are no results
    if (res.size() == 0) {
      Text notFound = new Text("No results found.");
      notFound.setFont(openSans14);
      resVBox.getChildren().add(notFound);
      return;
    }

    int x = 6;
    if (results.size() - (6 * page) < 6)
      x = res.size() - page * 6;
    for (int i = page * 6; i < page * 6 + x; i++) {

      Hyperlink link = new Hyperlink(res.get(i).getTitle());
      link.setWrapText(true);
      link.setFont(openSans18);
      openArticle(link);

      Text desc = new Text(res.get(i).getHighlight());
      desc.setWrappingWidth(600);
      desc.setFont(openSans14);

      WebView webView = new WebView();
      WebEngine webEngine = webView.getEngine();
      webEngine.loadContent(
          "<body style=\"background-color:rgb(245,245,245)\">"
          + res.get(i).getHighlight() + "</body>");
      resVBox.getChildren().add(link);
      resVBox.setPadding(new Insets(0, 0, 0, 0));
      resVBox.getChildren().add(webView);
    }

    // Adjust buttons
    if (res.size() < 6)
      btnNext.setVisible(false);
    else
      btnNext.setVisible(true);
  }

  public void updateHistory(String query) {

    if (history.contains(query))
      return;

    history.addFirst(query);
  }

  private void openArticle(Hyperlink link) {

    link.setOnAction(e -> {
      String txt = link.getText();
      for (Result r : results) {
        if (r.getTitle().equals(txt)) {
          new ArticleViewer(r);
          break;
        }
      }
    });
  }


}
