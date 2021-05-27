package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import java.util.HashSet;
import java.util.Set;

public class WordCompletion {

  private final Set<String> history = new HashSet<>();
  private AutoCompletionBinding<String> autoComplete;

  @FXML
  private final TextField searchInput;

  public WordCompletion(TextField searchInput) {
    this.searchInput = searchInput;
    autoComplete = TextFields.bindAutoCompletion(searchInput, history);
  }

  public void updateHistory(String text) {
    history.add(text);

    if (autoComplete != null)
      autoComplete.dispose();
    autoComplete = TextFields.bindAutoCompletion(searchInput, history);
  }

}
