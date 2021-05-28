package view;

import controller.ResultsController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Result;

import java.util.Objects;

public class ArticleViewer {


  public ArticleViewer(Result r) {

    Font font18 = Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
        "/view/OpenSans-Regular.ttf")).toExternalForm(), 20);
    Font font14 = Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
        "/view/Poppins-Light.ttf")).toExternalForm(), 15);
    Font font12 = Font.loadFont(Objects.requireNonNull(ResultsController.class.getResource(
        "/view/Poppins-Light.ttf")).toExternalForm(), 13);

    Text title = new Text(r.getTitle());
    title.setFont(font18);
    title.setUnderline(true);
    title.setWrappingWidth(765);

    Text focus = new Text(r.getFocus());
    focus.setFont(font14);

    Text authorAndDate = new Text("By " + r.getAuthor() + ", " + r.getDate());
    authorAndDate.setFont(font14);

    Text contents = new Text(r.getContents());
    contents.setFont(font12);
    contents.setWrappingWidth(765);

    // Add information in vbox
    VBox vbox = new VBox();
    vbox.getChildren().add(title);
    vbox.getChildren().add(new Text("\n"));
    vbox.getChildren().add(focus);
    vbox.getChildren().add(authorAndDate);
    vbox.getChildren().add(contents);

    // Pane settings
    ScrollPane pane = new ScrollPane();
    pane.setPadding(new Insets(10));
    pane.setContent(vbox);

    // Stage settings
    Stage stage = new Stage(StageStyle.UTILITY);
    stage.setScene(new Scene(pane, 800, 600));
    stage.setTitle(r.getTitle());

    stage.show();
  }
}
