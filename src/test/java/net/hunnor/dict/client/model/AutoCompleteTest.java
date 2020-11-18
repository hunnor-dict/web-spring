package net.hunnor.dict.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
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
  void testLanguages() {
    Autocomplete autocomplete = new Autocomplete();
    autocomplete.setLanguages(new HashSet<>());
    assertEquals(0, autocomplete.getLanguages().size());
  }

}
