package net.hunnor.dict.client.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.http.Cookie;
import net.hunnor.dict.client.model.Contrib;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.Response;
import net.hunnor.dict.client.service.CaptchaService;
import net.hunnor.dict.client.service.MailerService;
import net.hunnor.dict.client.service.SearchService;
import net.hunnor.dict.client.service.ServiceException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CaptchaService captchaService;

  @MockBean
  private MailerService mailerService;

  @MockBean
  private SearchService searchService;

  @Test
  public void testAbout() throws Exception {
    Map<Language, Long> counts = new HashMap<>();
    counts.put(Language.HU, Long.valueOf(1));
    counts.put(Language.NB, Long.valueOf(1));
    given(searchService.counts()).willReturn(counts);
    mockMvc.perform(get("/about")).andExpect(status().isOk())
        .andExpect(model().attribute("hu", Long.valueOf(1)))
        .andExpect(model().attribute("nb", Long.valueOf(1)))
        .andExpect(view().name("views/about/index"));
  }

  @Test
  public void testAboutSearchError() throws Exception {
    given(searchService.counts()).willThrow(ServiceException.class);
    mockMvc.perform(get("/about")).andExpect(status().isOk())
    .andExpect(model().attributeDoesNotExist("hu"))
    .andExpect(model().attributeDoesNotExist("nb"))
        .andExpect(view().name("views/about/index"));
  }

  @Test
  public void testContrib() throws Exception {
    mockMvc.perform(get("/contrib"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/contrib/index"));
  }

  @Test
  public void testContribPost() throws Exception {
    mockMvc.perform(post("/contrib"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/contrib/index"));
  }

  @Test
  public void testContribCaptchaError() throws Exception {
    Contrib contrib = new Contrib("foo", "bar", "baz");
    given(captchaService.isResponseValid(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .willThrow(ServiceException.class);
    mockMvc.perform(post("/contrib")
        .param("spelling", contrib.getSpelling())
        .param("trans", contrib.getTrans())
        .param("comments", contrib.getComments()))
        .andExpect(status().isOk())
        .andExpect(model().attribute("hasCaptcha", true))
        .andExpect(model().attribute("hasInput", true))
        .andExpect(view().name("views/contrib/index"));
  }

  @Test
  public void testContribCaptchaInvalid() throws Exception {
    Contrib contrib = new Contrib("foo", "bar", "baz");
    mockMvc.perform(post("/contrib")
        .param("spelling", contrib.getSpelling())
        .param("trans", contrib.getTrans())
        .param("comments", contrib.getComments()))
        .andExpect(status().isOk())
        .andExpect(model().attribute("hasCaptcha", true))
        .andExpect(model().attribute("hasInput", true))
        .andExpect(view().name("views/contrib/index"));
  }

  @Test
  public void testContribCaptchaValid() throws Exception {
    Contrib contrib = new Contrib("foo", "bar", "baz");
    given(captchaService.isResponseValid(
        ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(true);
    mockMvc.perform(post("/contrib")
        .param("spelling", contrib.getSpelling())
        .param("trans", contrib.getTrans())
        .param("comments", contrib.getComments()))
        .andExpect(status().isOk())
        .andExpect(model().attribute("hasCaptcha", true))
        .andExpect(model().attribute("hasInput", true))
        .andExpect(view().name("views/contrib/index"));
  }

  @Test
  public void testContribMailerError() throws Exception {
    Contrib contrib = new Contrib("foo", "bar", "baz");
    given(captchaService.isResponseValid(
        ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(true);
    doThrow(ServiceException.class).when(mailerService).send(ArgumentMatchers.any(Contrib.class));
    mockMvc.perform(post("/contrib")
        .param("spelling", contrib.getSpelling())
        .param("trans", contrib.getTrans())
        .param("comments", contrib.getComments()))
        .andExpect(status().isOk())
        .andExpect(model().attribute("hasCaptcha", true))
        .andExpect(model().attribute("hasInput", true))
        .andExpect(view().name("views/contrib/index"));
  }

  @Test
  public void testCookies() throws Exception {
    mockMvc.perform(get("/cookies"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/cookies/index"));
  }

  @Test
  public void testDownload() throws Exception {
    mockMvc.perform(get("/download"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/download/index"));
  }

  @Test
  public void testSearch() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/search/index"));
  }

  @Test
  public void testSearchEmptyTerm() throws Exception {
    mockMvc.perform(get("/")
        .param("term", ""))
        .andExpect(status().isOk())
        .andExpect(view().name("views/search/index"));
  }

  @Test
  public void testSearchNonEmptyTerm() throws Exception {
    Map<Language, Response> map = new HashMap<>();
    Response responseHu = new Response();
    responseHu.setResults(new HashSet<>());
    responseHu.setSuggestions(new HashSet<>());
    map.put(Language.HU, responseHu);
    Response responseNb = new Response();
    responseNb.setResults(new HashSet<>());
    responseNb.setSuggestions(new HashSet<>());
    map.put(Language.NB, responseNb);
    given(searchService.search(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .willReturn(map);
    mockMvc.perform(get("/")
        .param("term", "foo"))
        .andExpect(status().isOk())
        .andExpect(view().name("views/search/index"));
  }

  @Test
  public void testSearchError() throws Exception {
    given(searchService.search(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .willThrow(ServiceException.class);
    mockMvc.perform(get("/")
        .param("term", "foo"))
        .andExpect(status().isOk())
        .andExpect(model().attributeDoesNotExist("responses"))
        .andExpect(view().name("views/search/index"));
  }

  @Test
  public void testSearchCookiesViewInline() throws Exception {
    mockMvc.perform(get("/")
        .cookie(new Cookie("view", "inline")))
        .andExpect(status().isOk())
        .andExpect(view().name("views/search/index"))
        .andExpect(model().attributeDoesNotExist("view"));
  }

  @Test
  public void testSearchCookiesViewTree() throws Exception {
    mockMvc.perform(get("/")
        .cookie(new Cookie("view", "tree")))
        .andExpect(status().isOk())
        .andExpect(view().name("views/search/index"))
        .andExpect(model().attributeExists("view"))
        .andExpect(model().attribute("view", equalTo("tree")));
  }

}
