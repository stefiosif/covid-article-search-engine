package model;

public class Result {

  private final String title, author, date, focus, contents, highlight;

  public Result(String title, String author, String date,
                String focus, String contents, String highlight){

    this.title = title;
    this.author = author;
    this.date = date;
    this.focus = focus;
    this.contents = contents;
    this.highlight = highlight;
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

  public String getHighlight() {
    return highlight;
  }
}
