package net.hunnor.dict.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ResultTest {

  @Test
  void testConstructor() {
    Result result = new Result("id", "html");
    assertEquals("id", result.getId());
    assertEquals("html", result.getHtml());
  }

  @Test
  void testId() {
    Result result = new Result();
    result.setId("id");
    assertEquals("id", result.getId());
  }

  @Test
  void testHtml() {
    Result result = new Result();
    result.setHtml("html");
    assertEquals("html", result.getHtml());
  }

}
