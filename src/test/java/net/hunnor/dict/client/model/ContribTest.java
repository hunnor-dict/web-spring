package net.hunnor.dict.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ContribTest {

  @Test
  public void constructorWithAllFields() {
    Contrib contrib = new Contrib("foo", "bar", "baz");
    assertEquals("foo", contrib.getSpelling());
    assertEquals("bar", contrib.getTrans());
    assertEquals("baz", contrib.getComments());
  }

  @Test
  public void hasNoInputIfEmpty() {
    Contrib contrib = new Contrib();
    assertFalse(contrib.hasInput());
  }

  @Test
  public void hasContentIfHasSpelling() {
    Contrib contrib = new Contrib();
    contrib.setSpelling("foo");
    assertTrue(contrib.hasInput());
  }

  @Test
  public void hasContentIfHasTrans() {
    Contrib contrib = new Contrib();
    contrib.setTrans("foo");
    assertTrue(contrib.hasInput());
  }

  @Test
  public void hasContentIfHasComments() {
    Contrib contrib = new Contrib();
    contrib.setComments("foo");
    assertTrue(contrib.hasInput());
  }

  @Test
  public void hasNoContentIfAllFieldsEmpty() {
    Contrib contrib = new Contrib();
    contrib.setSpelling("");
    contrib.setTrans("");
    contrib.setComments("");
    assertFalse(contrib.hasInput());
  }

}
