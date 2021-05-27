package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.NextQuery;
import model.Result;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import view.ArticleViewer;
import view.Main;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
    nextQuery.setSort(sortBox.getSelectionModel().getSelectedItem());
  }

  @FXML
  private void chooseFocus() {
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
      link.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
          "/view/OpenSans-Regular.ttf")).toExternalForm(), 12));
      searchSuggestion(link);
      suggestHBox.getChildren().add(link);
    }

    var elapsedTime = System.nanoTime() - start;
    double searchingTime = (double) elapsedTime / 1_000_000_000;
    metaText.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
        "/view/OpenSans-Regular.ttf")).toExternalForm(), 16));
    metaText.setText(results.size() + " search results (" + searchingTime + " seconds)");

  }

  public void showResults(List<Result> res, int page) {

    this.results = res;

    // Clear previous search/page results
    resVBox.getChildren().clear();

    // Check if no results
    if (res.size() == 0) {
      Text notFound = new Text("No results found.");
      notFound.setFont(Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
          "/view/OpenSans-Regular.ttf")).toExternalForm(), 14));
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
          "/view/OpenSans-Regular.ttf")).toExternalForm(), 18));
      openArticle(link);
      resVBox.getChildren().add(link);

      // Use bold font for the query
      String highlight = res.get(i).getHighlight();
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
