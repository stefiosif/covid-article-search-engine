package com.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import com.model.AdvancedSearch;
import com.model.Constants;
import com.model.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import com.view.ArticleViewer;
import com.view.Main;
import java.io.IOException;
import java.util.*;

public class ResultsController {

  @FXML
  private VBox resVBox;

  @FXML
  private TextField searchInput;

  @FXML
  private Button btnPrev;
  @FXML
  private Button btnNext;

  @FXML
  private Text metaText;

  @FXML
  private HBox suggestHBox;

  @FXML
  private ComboBox<String> sortBox;
  @FXML
  private ComboBox<String> focusBox;

  private int pageId = 0;
  private List<Result> results;
  private AdvancedSearch advancedSearch;
  private WordCompletion wc;

  public void initialize() {

    wc = new WordCompletion(searchInput);

    // Create Advanced search options
    ObservableList<String> sortOptions = FXCollections.observableArrayList(
        "Date", "Relevance");
    sortBox.setItems(sortOptions);
    ObservableList<String> focusOptions = FXCollections.observableArrayList(
        "All", "Business", "General", "Consumer", "Finance", "Science", "Tech");
    focusBox.setItems(focusOptions);

    // Create nextQuery
    advancedSearch = new AdvancedSearch();
  }

  @FXML
  public void searchAdvanced() throws IOException, ParseException, InvalidTokenOffsetsException {

    String query = searchInput.getText();
    if (query == null || query.equals("")) return;

    search(query);
  }

  @FXML
  public void showPrevious() {

    resVBox.getChildren().clear();
    showResults(results, --pageId);

    btnNext.setVisible(true);
    if (pageId == 0)
      btnPrev.setVisible(false);
  }

  @FXML
  public void showNext() {

    resVBox.getChildren().clear();
    showResults(results, ++pageId);

    btnPrev.setVisible(true);
    if (results.size() - (7 * pageId) < 7)
      btnNext.setVisible(false);
  }

  @FXML
  private void sortBy() {
    advancedSearch.setSort(sortBox.getSelectionModel().getSelectedItem().toLowerCase());
  }

  @FXML
  private void chooseFocus() {
    advancedSearch.setFocus(focusBox.getSelectionModel().getSelectedItem().toLowerCase());
  }

  public void search(String query) throws IOException, ParseException, InvalidTokenOffsetsException {

    searchInput.setText(query);

    long start = System.nanoTime();
    List<Result> resultsPool
        = Main.getInstance().getSearcher().search(query, advancedSearch);

    var elapsedTime = System.nanoTime() - start;
    double searchingTime = (double) elapsedTime / 1_000_000_000;
    metaText.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
            Constants.OS_FONT)).toExternalForm(), 16));
    metaText.setText(resultsPool.size() + " search results (" + searchingTime + " seconds)");

    wc.updateHistory(query);
    showResults(resultsPool, 0);

    // Set suggestions
    String[] suggestions = Main.getInstance().getIndexer().getSpellChecker().suggestSimilar(query, 4);
    suggestHBox.getChildren().clear();

    for (var s : suggestions) {
      Hyperlink link = new Hyperlink(s);
      link.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
              Constants.OS_FONT)).toExternalForm(), 13));
      searchSuggestion(link);
      suggestHBox.getChildren().add(link);
    }
  }

  public void showResults(List<Result> res, int page) {

    this.results = res;

    // Clear previous search/page results
    resVBox.getChildren().clear();

    // Check if no results
    if (res.isEmpty()) {
      Text notFound = new Text("No results found.");
      notFound.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
              Constants.OS_FONT)).toExternalForm(), 14));
      resVBox.getChildren().add(notFound);
      return;
    }

    // Create 7 link/description results for the current page
    int window = 7;
    if (results.size() - (7 * page) < 7)
      window = res.size() - page * 7;

    for (int i = page * 7; i < page * 7 + window; i++) {

      Hyperlink link = new Hyperlink(res.get(i).getTitle());
      link.setWrapText(true);
      link.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
              Constants.OS_FONT)).toExternalForm(), 18));
      openArticle(link);
      resVBox.getChildren().add(link);

      String highlight = res.get(i).getDate() + " |" + res.get(i).getHighlight();

      Text p1 = new Text(highlight.substring(0, highlight.indexOf("<B>")));
      p1.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
              "/view/OpenSans-Regular.ttf")).toExternalForm(), 14));
      Text p2 = new Text(highlight.substring(highlight.indexOf("<B>") + 3, highlight.indexOf("</B>")));
      p2.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
              "/view/OpenSans-Bold.ttf")).toExternalForm(), 14));
      Text p3 = new Text(highlight.substring(highlight.indexOf("</B>") + 4));
      p3.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
              "/view/OpenSans-Regular.ttf")).toExternalForm(), 14));

      TextFlow flow = new TextFlow();
      flow.getChildren().addAll(p1, p2, p3);

      resVBox.getChildren().add(flow);
    }

    // Adjust buttons
    btnNext.setVisible(res.size() >= 7);
    if (page == 0)
      btnPrev.setVisible(false);
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

  private void searchSuggestion(Hyperlink link) {
    link.setOnAction(e -> {
      String txt = link.getText();
      try {
        search(txt);
      } catch (IOException | ParseException | InvalidTokenOffsetsException ioException) {
        ioException.printStackTrace();
      }
    });
  }

}
