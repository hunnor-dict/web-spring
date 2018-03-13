package net.hunnor.dict.client.model;

import java.util.Set;

public class Response {

  private Set<Result> results;

  private Set<String> suggestions;

  public Set<Result> getResults() {
    return results;
  }

  public void setResults(Set<Result> results) {
    this.results = results;
  }

  public Set<String> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(Set<String> suggestions) {
    this.suggestions = suggestions;
  }

}
