package net.hunnor.dict.client.model;

public class Result {

  private String id;

  private String html;

  public Result() {
  }

  public Result(String id, String html) {
    this.id = id;
    this.html = html;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

}
