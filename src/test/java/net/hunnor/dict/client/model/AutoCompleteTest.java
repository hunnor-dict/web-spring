package net.hunnor.dict.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AutoCompleteTest {

  @Test
  void testValue() {
    Autocomplete autocomplete = new Autocomplete();
    autocomplete.setValue("foo");
    assertEquals("foo", autocomplete.getValue());
  }

  @Test
  void testPrefix() {
    Autocomplete autocomplete = new Autocomplete();
    autocomplete.setPrefix(true);
    assertTrue(autocomplete.isPrefix());
  }

  @Test
  void testLanguagesEmpty() {
    Autocomplete autocomplete = new Autocomplete();
    assertNull(autocomplete.getLanguages());
  }

  @Test
  void testLanguages() {
    Autocomplete autocomplete = new Autocomplete();
    autocomplete.addLanguage(Language.HU);
    assertEquals(1, autocomplete.getLanguages().size());
  }

}
