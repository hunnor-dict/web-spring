package net.hunnor.dict.client.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ContribTest {

  @Test
  public void constructorWithAllFields() {
    Contrib contrib = new Contrib("foo", "bar", "baz", "qux");
    assertEquals("foo", contrib.getSpelling());
    assertEquals("bar", contrib.getInfl());
    assertEquals("baz", contrib.getTrans());
    assertEquals("qux", contrib.getComments());
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
  public void hasContentIfHasInfl() {
    Contrib contrib = new Contrib();
    contrib.setInfl("foo");
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
    contrib.setInfl("");
    contrib.setTrans("");
    contrib.setComments("");
    assertFalse(contrib.hasInput());
  }

}
