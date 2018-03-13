package net.hunnor.dict.client.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashSet;

public class AutoCompleteTest {

  @Test
  public void testValue() {
    Autocomplete autocomplete = new Autocomplete();
    autocomplete.setValue("foo");
    assertEquals("foo", autocomplete.getValue());
  }

  @Test
  public void testPrefix() {
    Autocomplete autocomplete = new Autocomplete();
    autocomplete.setPrefix(true);
    assertTrue(autocomplete.isPrefix());
  }

  @Test
  public void testLanguages() {
    Autocomplete autocomplete = new Autocomplete();
    autocomplete.setLanguages(new HashSet<>());
    assertEquals(0, autocomplete.getLanguages().size());
  }

}
