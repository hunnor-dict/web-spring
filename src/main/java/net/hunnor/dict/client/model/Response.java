package net.hunnor.dict.client.model;

import java.text.Collator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class Response {

  private Set<Result> results;

  private Set<String> suggestions;

  public Response(Collator collator) {
    suggestions = new TreeSet<>(collator);
    results = new LinkedHashSet<>();
  }

  public Set<Result> getResults() {
    return new LinkedHashSet<>(results);
  }

  /**
   * Add a result to the result set.
   * @param result the result to add
   */
  public void addResult(Result result) {
    if (results == null) {
      results = new LinkedHashSet<>();
    }
    results.add(result);
  }

  public Set<String> getSuggestions() {
    return new TreeSet<>(suggestions);
  }

  /**
   * Add a suggestion to the suggestion set.
   * @param suggestion the suggestion to add
   */
  public void addSuggestion(String suggestion) {
    if (suggestions == null) {
      suggestions = new TreeSet<>();
    }
    suggestions.add(suggestion);
  }

}
