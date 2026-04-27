package net.hunnor.dict.client.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Model for an autocomplete suggestion for a search query.
 */
public class Autocomplete {

  private String value;

  private boolean prefix;

  private Set<Language> languages;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isPrefix() {
    return prefix;
  }

  public void setPrefix(boolean prefix) {
    this.prefix = prefix;
  }

  public Set<Language> getLanguages() {
    return languages == null ? null : new HashSet<>(languages);
  }

  /**
   * Add a language to the language set.
   *
   * @param language the language to add
   */
  public void addLanguage(Language language) {
    if (languages == null) {
      languages = new HashSet<>();
    }
    languages.add(language);
  }

}
