package model;

public class Result {

  private final String title, author, date, focus, contents;
  private String highlight;

  public Result(String title, String author, String date, String focus, String contents){

    this.title = title;
    this.author = author;
    this.date = date;
    this.focus = focus;
    this.contents = contents;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public String getDate() {
    return date;
  }

  public String getFocus() {
    return focus;
  }

  public String getContents() {
    return contents;
  }

  public void createHighlight(String s) {
    this.highlight = s;
  }

  public String getHighlight() {
    return highlight;
  }
}
