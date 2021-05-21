package model;

public class Result {

  private final String title, focus;

  public Result(String title, String focus){

    this.title = title;
    this.focus = focus;
  }

  public String getTitle() {
    return title;
  }

  public String getFocus() {
    return focus;
  }
}
