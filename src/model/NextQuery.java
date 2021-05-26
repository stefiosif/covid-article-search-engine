package model;

public class NextQuery {

  private String sort, focus;

  public NextQuery() {
    this.sort = "relevance";
    this.focus = "all";
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public void setFocus(String focus) {
    this.focus = focus;
  }

  public String getSortBy() {
    return sort;
  }

  public String getFocus() {
    return focus;
  }
}