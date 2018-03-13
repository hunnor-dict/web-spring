package net.hunnor.dict.client.model;

public class Contrib {

  private String spelling;

  private String infl;

  private String trans;

  private String comments;

  public Contrib() {
  }

  /**
   * Constructor with all fields.
   * @param spelling value for field spelling
   * @param infl value for field infl
   * @param trans value for field trans
   * @param comments value for field comments
   */
  public Contrib(String spelling, String infl, String trans, String comments) {
    this.spelling = spelling;
    this.infl = infl;
    this.trans = trans;
    this.comments = comments;
  }

  public String getSpelling() {
    return spelling;
  }

  public void setSpelling(String spelling) {
    this.spelling = spelling;
  }

  public String getInfl() {
    return infl;
  }

  public void setInfl(String infl) {
    this.infl = infl;
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
    return hasSpelling() || hasInfl() || hasTrans() || hasComments();
  }

  private boolean hasSpelling() {
    return spelling != null && !spelling.isEmpty();
  }

  private boolean hasInfl() {
    return infl != null && !infl.isEmpty();
  }

  private boolean hasTrans() {
    return trans != null && !trans.isEmpty();
  }

  private boolean hasComments() {
    return comments != null && !comments.isEmpty();
  }

}
