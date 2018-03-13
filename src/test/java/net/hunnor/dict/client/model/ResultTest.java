package net.hunnor.dict.client.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResultTest {

  @Test
  public void testConstructor() {
    Result result = new Result("id", "html");
    assertEquals("id", result.getId());
    assertEquals("html", result.getHtml());
  }

  @Test
  public void testId() {
    Result result = new Result();
    result.setId("id");
    assertEquals("id", result.getId());
  }

  @Test
  public void testHtml() {
    Result result = new Result();
    result.setHtml("html");
    assertEquals("html", result.getHtml());
  }

}
