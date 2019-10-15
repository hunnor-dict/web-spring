package net.hunnor.dict.client.model;

public class Contrib {

  private String spelling;

  private String trans;

  private String comments;

  public Contrib() {
  }

  /**
   * Constructor with all fields.
   * @param spelling value for field spelling
   * @param trans value for field trans
   * @param comments value for field comments
   */
  public Contrib(String spelling, String trans, String comments) {
    this.spelling = spelling;
    this.trans = trans;
    this.comments = comments;
  }

  public String getSpelling() {
    return spelling;
  }

  public void setSpelling(String spelling) {
    this.spelling = spelling;
  }

  public String getTrans() {
    return trans;
  }

  public void setTrans(String trans) {
    this.trans = trans;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public boolean hasInput() {
    return hasSpelling() || hasTrans() || hasComments();
  }

  private boolean hasSpelling() {
    return spelling != null && !spelling.isEmpty();
  }

  private boolean hasTrans() {
    return trans != null && !trans.isEmpty();
  }

  private boolean hasComments() {
    return comments != null && !comments.isEmpty();
  }

}
