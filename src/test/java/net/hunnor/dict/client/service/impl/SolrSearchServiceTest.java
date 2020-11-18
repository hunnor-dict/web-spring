package net.hunnor.dict.client.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

import ch.qos.logback.classic.Level;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.hunnor.dict.client.model.Autocomplete;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.Response;
import net.hunnor.dict.client.service.SearchService;
import net.hunnor.dict.client.service.ServiceException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class SolrSearchServiceTest {

  @Autowired
  private SearchService searchService;

  @SpyBean
  private SolrClient solrClient;

  @TestConfiguration
  public static class SolrTestConfiguration {

    /**
     * Embedded Solr server for the tests.
     * @return an embedded Solr server with sample documents
     * @throws SolrServerException if an error occurs
     * @throws IOException if an error occurs
     */
    @Bean
    public SolrClient solrClient() throws SolrServerException, IOException {

      URL url = getClass().getResource("/solr");
      File file = new File(url.getFile());
      Path path = file.toPath();

      SolrClient solrClient = new EmbeddedSolrServer(path, "hunnor.hu");
      solrClient.deleteByQuery("hunnor.hu", "*:*");
      solrClient.deleteByQuery("hunnor.nb", "*:*");
      solrClient.commit("hunnor.hu");
      solrClient.commit("hunnor.nb");

      SolrInputDocument document = new SolrInputDocument();
      document.addField("id", "1");
      document.addField("spellings", "foo");
      document.addField("roots", "foo");
      document.addField("html", "<p>foo</p>");
      solrClient.add("hunnor.hu", document);

      document = new SolrInputDocument();
      document.addField("id", "2");
      document.addField("spellings", "bar");
      document.addField("roots", "bar");
      document.addField("html", "<p>bar</p>");
      solrClient.add("hunnor.hu", document);

      document = new SolrInputDocument();
      document.addField("id", "3");
      document.addField("spellings", "corge");
      document.addField("roots", "corge");
      document.addField("html", "<p>Corge</p>");
      solrClient.add("hunnor.hu", document);

      document = new SolrInputDocument();
      document.addField("id", "4");
      document.addField("spellings", "corge");
      document.addField("roots", "corge");
      document.addField("html", "<p>corge</p>");
      solrClient.add("hunnor.hu", document);

      document = new SolrInputDocument();
      document.addField("id", "1");
      document.addField("spellings", "bar");
      document.addField("roots", "bar");
      document.addField("html", "<p>bar</p>");
      solrClient.add("hunnor.nb", document);

      document = new SolrInputDocument();
      document.addField("id", "2");
      document.addField("spellings", "baz");
      document.addField("roots", "baz");
      document.addField("forms", "qux");
      document.addField("html", "<p>baz qux</p>");
      solrClient.add("hunnor.nb", document);

      solrClient.commit("hunnor.hu");
      solrClient.commit("hunnor.nb");

      return solrClient;

    }

  }

  @Test
  void testCounts() throws ServiceException {
    Map<Language, Long> counts = searchService.counts();
    assertNotNull(counts);
    assertEquals(Long.valueOf(4), counts.get(Language.HU));
    assertEquals(Long.valueOf(2), counts.get(Language.NB));
  }

  @Test
  void testCountsError() throws ServiceException, SolrServerException, IOException {
    assertThrows(ServiceException.class, () -> {
      doThrow(IOException.class).when(solrClient).query(
          ArgumentMatchers.anyString(), ArgumentMatchers.any(SolrQuery.class));
      searchService.counts();
    });
  }

  // Search for roots, with match in roots
  @Test
  void testSearchRootsMatchRoots() throws ServiceException {
    Map<Language, Response> results = searchService.search("foo", "roots");
    assertNotNull(results);
  }

  // Search for roots, with match in forms
  @Test
  void testSearchRootsMatchForms() throws ServiceException {
    Map<Language, Response> results = searchService.search("qux", "roots");
    assertNotNull(results);
  }

  // Search for roots, with no match
  @Test
  void testSearchRootsNoMatch() throws ServiceException {
    Map<Language, Response> results = searchService.search("quux", "roots");
    assertNotNull(results);
  }

  // Search for forms, with match in roots
  @Test
  void testSearchFormsMatchRoots() throws ServiceException {
    Map<Language, Response> results = searchService.search("foo", "forms");
    assertNotNull(results);
  }

  // Search for forms, with match in forms
  @Test
  void testSearchFormsMatchForms() throws ServiceException {
    Map<Language, Response> results = searchService.search("qux", "forms");
    assertNotNull(results);
  }

  // Search for forms, with no match
  @Test
  void testSearchFormsNoMatch() throws ServiceException {
    Map<Language, Response> results = searchService.search("quux", "forms");
    assertNotNull(results);
  }

  // Full search with match in roots match
  @Test
  void testSearchFullMatchRoots() throws ServiceException {
    Map<Language, Response> results = searchService.search("foo", "full");
    assertNotNull(results);
  }

  // Full search with match in roots match
  @Test
  void testSearchFullMatchForms() throws ServiceException {
    Map<Language, Response> results = searchService.search("qux", "full");
    assertNotNull(results);
  }

  // Full search with no match
  @Test
  void testSearchFullNoMatch() throws ServiceException {
    Map<Language, Response> results = searchService.search("fooo", "full");
    assertNotNull(results);
  }

  @Test
  void testHighlightingMismatch() throws ServiceException {
    Map<Language, Response> results = searchService.search("corge", "roots");
    assertNotNull(results);
  }

  @Test
  void testSearchPhrase() throws ServiceException {
    Map<Language, Response> results = searchService.search("foo bar baz", "roots");
    assertNotNull(results);
  }

  @Test
  void testSearchError() throws SolrServerException, IOException, ServiceException {
    assertThrows(ServiceException.class, () -> {
      doThrow(IOException.class).when(solrClient).query(
          ArgumentMatchers.anyString(), ArgumentMatchers.any(SolrQuery.class));
      searchService.search("foo", "roots");
    });
  }

  @Test
  void testSearchLogging() throws ServiceException {

    Logger logger = LoggerFactory.getLogger("net.hunnor.dict.client.log.searches");
    ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;

    Level level = logbackLogger.getLevel();
    assertNull(level);

    logbackLogger.setLevel(ch.qos.logback.classic.Level.ERROR);
    assertFalse(logger.isInfoEnabled());

    searchService.search("foo", "full");
    logbackLogger.setLevel(level);

  }

  @Test
  void testSuggestionsPrefix() throws ServiceException {
    List<Autocomplete> suggestions = searchService.suggest("fo");
    assertNotNull(suggestions);
    assertEquals(1, suggestions.size());
  }

  @Test
  void testSuggestionsPrefixMergeLanguages() throws ServiceException {
    List<Autocomplete> suggestions = searchService.suggest("ba");
    assertNotNull(suggestions);
    assertEquals(2, suggestions.size());
  }

  @Test
  void testSuggestionsSuggestion() throws ServiceException {
    List<Autocomplete> suggestions = searchService.suggest("bazz");
    assertNotNull(suggestions);
    assertEquals(1, suggestions.size());
    assertEquals("baz", suggestions.get(0).getValue());
  }

  @Test
  void testSuggestionsSuggestionMergeLanguage() throws ServiceException {
    List<Autocomplete> suggestions = searchService.suggest("barr");
    assertNotNull(suggestions);
    assertEquals(1, suggestions.size());
    assertEquals("bar", suggestions.get(0).getValue());
  }

  @Test
  void testSuggestionsNull() throws ServiceException {
    List<Autocomplete> suggestions = searchService.suggest(null);
    assertNotNull(suggestions);
    assertTrue(suggestions.isEmpty());
  }

  @Test
  void testSuggestionsLongTerm() throws ServiceException {
    List<Autocomplete> suggestions = searchService.suggest(
        "1234567890 1234567890 1234567890 1234567890 1234567890"
        + "1234567890 1234567890 1234567890 1234567890 1234567890");
    assertNotNull(suggestions);
    assertTrue(suggestions.isEmpty());
  }

  @Test
  void testSuggestionsError() throws ServiceException, SolrServerException, IOException {
    assertThrows(ServiceException.class, () -> {
      doThrow(IOException.class).when(solrClient).query(
          ArgumentMatchers.anyString(), ArgumentMatchers.any(SolrQuery.class));
      searchService.suggest("foo");
    });
  }

}
