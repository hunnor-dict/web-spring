package net.hunnor.dict.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ContribTest {

  @Test
  void constructorWithAllFields() {
    Contrib contrib = new Contrib("foo", "bar", "baz");
    assertEquals("foo", contrib.getSpelling());
    assertEquals("bar", contrib.getTrans());
    assertEquals("baz", contrib.getComments());
  }

  @Test
  void hasNoInputIfEmpty() {
    Contrib contrib = new Contrib();
    assertFalse(contrib.hasInput());
  }

  @Test
  void hasContentIfHasSpelling() {
    Contrib contrib = new Contrib();
    contrib.setSpelling("foo");
    assertTrue(contrib.hasInput());
  }

  @Test
  void hasContentIfHasTrans() {
    Contrib contrib = new Contrib();
    contrib.setTrans("foo");
    assertTrue(contrib.hasInput());
  }

  @Test
  void hasContentIfHasComments() {
    Contrib contrib = new Contrib();
    contrib.setComments("foo");
    assertTrue(contrib.hasInput());
  }

  @Test
  void hasNoContentIfAllFieldsEmpty() {
    Contrib contrib = new Contrib();
    contrib.setSpelling("");
    contrib.setTrans("");
    contrib.setComments("");
    assertFalse(contrib.hasInput());
  }

}
