package net.hunnor.dict.client.model;

import java.util.Set;

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
    return languages;
  }

  public void setLanguages(Set<Language> languages) {
    this.languages = languages;
  }

}
