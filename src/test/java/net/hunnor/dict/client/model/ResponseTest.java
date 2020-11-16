package net.hunnor.dict.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class ResponseTest {

  @Test
  public void testResults() {
    Response response = new Response();
    response.setResults(new HashSet<>());
    assertEquals(0, response.getResults().size());
  }

  @Test
  public void testSuggestions() {
    Response response = new Response();
    response.setSuggestions(new HashSet<>());
    assertEquals(0, response.getSuggestions().size());
  }

}
