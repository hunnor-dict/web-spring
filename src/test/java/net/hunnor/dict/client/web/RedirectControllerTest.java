package net.hunnor.dict.client.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(RedirectController.class)
public class RedirectControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testMobile() throws Exception {
    mockMvc.perform(get("/m")).andExpect(status().isFound());
  }

  @Test
  public void testNorwegian() throws Exception {
    mockMvc.perform(get("/no")).andExpect(status().isFound());
    mockMvc.perform(get("/no/about")).andExpect(status().isFound());
  }

  @Test
  public void testSearch() throws Exception {
    mockMvc.perform(get("/search")).andExpect(status().isFound());
  }

  @Test
  public void testGramm() throws Exception {
    mockMvc.perform(get("/gramm")).andExpect(status().isFound());
    mockMvc.perform(get("/gramm/pron")).andExpect(status().isFound());
  }

}
