package net.hunnor.dict.client.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hunnor.dict.client.model.Autocomplete;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.Response;
import net.hunnor.dict.client.model.Result;
import net.hunnor.dict.client.service.SearchService;
import net.hunnor.dict.client.service.ServiceException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SearchService searchService;

  @Test
  void testSuggest() throws Exception {
    Autocomplete foo = new Autocomplete();
    foo.setValue("foo");
    foo.setPrefix(true);
    foo.addLanguage(Language.HU);
    Autocomplete bar = new Autocomplete();
    bar.setValue("bar");
    bar.setPrefix(true);
    bar.addLanguage(Language.HU);
    bar.addLanguage(Language.NB);
    List<Autocomplete> list = new ArrayList<>();
    list.add(foo);
    list.add(bar);
    given(searchService.suggest(ArgumentMatchers.any()))
        .willReturn(list);
    mockMvc.perform(get("/suggest")
        .param("term", "foo"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].value", equalTo("foo")))
        .andExpect(jsonPath("$[1].languages", hasItem(equalTo("HU"))))
        .andExpect(jsonPath("$[1].languages", hasItem(equalTo("NB"))));
  }

  @Test
  void testSuggestError() throws Exception {
    given(searchService.suggest(ArgumentMatchers.any()))
        .willThrow(ServiceException.class);
    mockMvc.perform(get("/suggest")
        .param("term", "foo"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void testSuggestOpensearch() throws Exception {
    Autocomplete foo = new Autocomplete();
    foo.setValue("foo");
    Autocomplete bar = new Autocomplete();
    bar.setValue("bar");
    List<Autocomplete> list = new ArrayList<>();
    list.add(foo);
    list.add(bar);
    given(searchService.suggest(ArgumentMatchers.any()))
        .willReturn(list);
    mockMvc.perform(get("/opensearch/suggest")
        .param("term", "baz"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(
            MediaType.parseMediaType("application/x-suggestions+json")))
        .andExpect(jsonPath("$[0]", equalTo("baz")))
        .andExpect(jsonPath("$[1][0]", equalTo("foo")))
        .andExpect(jsonPath("$[1][1]", equalTo("bar")));
  }

  @Test
  void testSuggestOpensearchNullAutocomplete() throws Exception {
    given(searchService.suggest(ArgumentMatchers.any()))
        .willReturn(null);
    mockMvc.perform(get("/opensearch/suggest"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(
            MediaType.parseMediaType("application/x-suggestions+json")));
  }

  @Test
  void testSuggestOpensearchError() throws Exception {
    given(searchService.suggest(ArgumentMatchers.any()))
        .willThrow(ServiceException.class);
    mockMvc.perform(get("/opensearch/suggest"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(
            MediaType.parseMediaType("application/x-suggestions+json")));
  }

  @Test
  void testSearch() throws Exception {
    Response response = new Response(null);
    response.addResult(new Result("foo-1", "<b>foo</b>"));
    response.addSuggestion("foobar");
    Map<Language, Response> results = new HashMap<>();
    results.put(Language.HU, response);
    given(searchService.search(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .willReturn(results);
    mockMvc.perform(get("/search")
        .param("term", "foo")
        .param("match", "roots"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.HU.results[0].id", equalTo("foo-1")))
        .andExpect(jsonPath("$.HU.results[0].html", equalTo("<b>foo</b>")))
        .andExpect(jsonPath("$.HU.suggestions[0]", equalTo("foobar")));
  }

}
