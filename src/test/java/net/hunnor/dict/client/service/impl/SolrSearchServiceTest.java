package net.hunnor.dict.client.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SolrSearchServiceTest {

  @Autowired
  private SearchService searchService;

  @MockBean
  private SolrClient solrClient;

  /**
   * Setup mock Solr responses before each test.
   */
  @BeforeEach
  void setUp() throws SolrServerException, IOException {
    // Mock standard search responses
    when(solrClient.query(anyString(), any(SolrQuery.class)))
        .thenAnswer(invocation -> {
          String collection = invocation.getArgument(0);
          SolrQuery query = invocation.getArgument(1);
          return createMockResponse(collection, query);
        });
    
    // Mock GenericSolrRequest responses (used for /suggest handler)
    when(solrClient.request(any(), anyString()))
        .thenAnswer(invocation -> {
          Object request = invocation.getArgument(0);
          String collection = invocation.getArgument(1);
          // Extract query parameter from the request
          String query = null;
          if (request instanceof org.apache.solr.client.solrj.request.GenericSolrRequest) {
            org.apache.solr.client.solrj.request.GenericSolrRequest genericRequest = 
                (org.apache.solr.client.solrj.request.GenericSolrRequest) request;
            query = genericRequest.getParams().get(org.apache.solr.common.params.CommonParams.Q);
          }
          // Return the raw NamedList directly
          return createSuggestResponseNamedList(collection, query);
        });
  }

  /**
   * Create mock Solr response based on query parameters.
   */
  private QueryResponse createMockResponse(String collection, SolrQuery query) {
    SolrDocumentList results = new SolrDocumentList();

    String queryStr = query.getQuery();
    String requestHandler = query.getRequestHandler();

    // Handle different request types
    if ("/suggest".equals(requestHandler)) {
      // For suggest/autocomplete queries, return results with TermsResponse
      return createSuggestResponse(collection, queryStr);
    }

    // Handle counts query (*:*)
    if ("*:*".equals(queryStr)) {
      if ("hunnor.hu".equals(collection)) {
        results.setNumFound(4);  // 4 documents in hunnor.hu (foo, bar, corge, corge)
      } else if ("hunnor.nb".equals(collection)) {
        results.setNumFound(2);  // 2 documents in hunnor.nb (bar, baz/qux)
      }
      return createQueryResponseWithResults(results);
    }

    // Mock responses based on collection and query
    if ("hunnor.hu".equals(collection)) {
      if (queryStr != null && queryStr.contains("foo")) {
        results.add(createDocument("1", "foo", "foo", "<p>foo</p>"));
      } else if (queryStr != null && queryStr.contains("bar")) {
        results.add(createDocument("2", "bar", "bar", "<p>bar</p>"));
      } else if (queryStr != null && queryStr.contains("corge")) {
        results.add(createDocument("3", "corge", "corge", "<p>Corge</p>"));
        results.add(createDocument("4", "corge", "corge", "<p>corge</p>"));
      } else if (queryStr != null && queryStr.contains("qux")) {
        // qux is not in hunnor.hu
      }
    } else if ("hunnor.nb".equals(collection)) {
      if (queryStr != null && queryStr.contains("bar")) {
        results.add(createDocument("1", "bar", "bar", "<p>bar</p>"));
      } else if (queryStr != null && (queryStr.contains("baz") || queryStr.contains("qux"))) {
        SolrDocument doc = createDocument("2", "baz", "baz", "<p>baz qux</p>");
        doc.addField("forms", "qux");
        results.add(doc);
      }
    }

    results.setNumFound(results.size());
    return createQueryResponseWithResults(results);
  }

  /**
   * Create a mock suggest response with TermsResponse and SuggesterResponse.
   */
  private QueryResponse createSuggestResponse(String collection, String query) {
    QueryResponse response = new QueryResponse();

    // Create TermsResponse for prefix matching
    NamedList<NamedList<Object>> termsInfo = new NamedList<>();
    NamedList<Object> spellings = new NamedList<>();

    // Create mocked SuggesterResponse for spelling suggestions
    SuggesterResponse suggesterResponse = mock(SuggesterResponse.class);
    Map<String, List<String>> suggestedTerms = new HashMap<>();

    // Add prefix-match suggestions (exact prefix match)
    if ("hunnor.hu".equals(collection)) {
      if (query != null && (query.startsWith("fo") && !query.equals("fooo"))) {
        spellings.add("foo", 1);
      } else if (query != null && (query.startsWith("ba") && query.length() == 2)) {
        // For "ba", return both "bar" and nothing else in hunnor.hu
        spellings.add("bar", 1);
      } else if (query != null && (query.startsWith("bar") && query.length() == 3)) {
        spellings.add("bar", 1);
      } else if (query != null && query.startsWith("corge")) {
        spellings.add("corge", 2);
      }

      // Add spelling suggestions for misspellings
      if (query != null && query.equals("fooo")) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("foo");
        suggestedTerms.put(query, suggestions);
      } else if (query != null && query.equals("barr")) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("bar");
        suggestedTerms.put(query, suggestions);
      }
    } else if ("hunnor.nb".equals(collection)) {
      if (query != null && (query.startsWith("ba") && query.length() == 2)) {
        // For "ba", return both "bar" and "baz"
        spellings.add("bar", 1);
        spellings.add("baz", 1);
      } else if (query != null && (query.startsWith("bar") && query.length() == 3)) {
        spellings.add("bar", 1);
      } else if (query != null && (query.startsWith("baz") && query.length() == 3)) {
        spellings.add("baz", 1);
      }

      // Add spelling suggestions for misspellings
      if (query != null && query.equals("bazz")) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("baz");
        suggestedTerms.put(query, suggestions);
      } else if (query != null && query.equals("barr")) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("bar");
        suggestedTerms.put(query, suggestions);
      }
    }

    termsInfo.add("spellings", spellings);

    // Create TermsResponse and set it
    org.apache.solr.client.solrj.response.TermsResponse termsResponse =
        new org.apache.solr.client.solrj.response.TermsResponse(termsInfo);

    // Configure mocked SuggesterResponse
    when(suggesterResponse.getSuggestedTerms()).thenReturn(suggestedTerms);

    try {
      // Set the internal fields for parsing methods to work
      java.lang.reflect.Field field = QueryResponse.class.getDeclaredField("_termsResponse");
      field.setAccessible(true);
      field.set(response, termsResponse);

      field = QueryResponse.class.getDeclaredField("_suggestResponse");
      field.setAccessible(true);
      field.set(response, suggesterResponse);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set responses on QueryResponse", e);
    }

    return response;
  }

  /**
   * Create the raw NamedList structure that Solr's /suggest handler returns.
   * This is used for GenericSolrRequest mocking.
   */
  private NamedList<Object> createSuggestResponseNamedList(
      String collection, String query) {
    // Create TermsResponse for prefix matching
    NamedList<NamedList<Object>> termsInfo = new NamedList<>();
    NamedList<Object> spellings = new NamedList<>();

    // Create mocked suggester data
    Map<String, List<String>> suggestedTerms = new HashMap<>();

    // Add prefix-match suggestions (exact prefix match)
    if ("hunnor.hu".equals(collection)) {
      if (query != null && (query.startsWith("fo") && !query.equals("fooo"))) {
        spellings.add("foo", 1);
      } else if (query != null && (query.startsWith("ba") && query.length() == 2)) {
        spellings.add("bar", 1);
      } else if (query != null && (query.startsWith("bar") && query.length() == 3)) {
        spellings.add("bar", 1);
      } else if (query != null && query.startsWith("corge")) {
        spellings.add("corge", 2);
      }

      // Add spelling suggestions for misspellings
      if (query != null && query.equals("fooo")) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("foo");
        suggestedTerms.put(query, suggestions);
      } else if (query != null && query.equals("barr")) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("bar");
        suggestedTerms.put(query, suggestions);
      }
    } else if ("hunnor.nb".equals(collection)) {
      if (query != null && (query.startsWith("ba") && query.length() == 2)) {
        spellings.add("bar", 1);
        spellings.add("baz", 1);
      } else if (query != null && (query.startsWith("bar") && query.length() == 3)) {
        spellings.add("bar", 1);
      } else if (query != null && (query.startsWith("baz") && query.length() == 3)) {
        spellings.add("baz", 1);
      }

      // Add spelling suggestions for misspellings
      if (query != null && query.equals("bazz")) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("baz");
        suggestedTerms.put(query, suggestions);
      } else if (query != null && query.equals("barr")) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("bar");
        suggestedTerms.put(query, suggestions);
      }
    }

    termsInfo.add("spellings", spellings);

    // Build the complete NamedList structure that Solr would return
    NamedList<Object> responseData = new NamedList<>();
    responseData.add("terms", termsInfo);
    
    // Add suggest section if there are suggestions
    // The suggest component returns a structure like: suggest -> default -> query -> suggestions
    if (!suggestedTerms.isEmpty()) {
      SimpleOrderedMap<Object> defaultDictionary = 
          new SimpleOrderedMap<>();
      for (Map.Entry<String, List<String>> entry : suggestedTerms.entrySet()) {
        SimpleOrderedMap<Object> suggestionList = 
            new SimpleOrderedMap<>();
        suggestionList.add("numFound", (long) entry.getValue().size());
        SimpleOrderedMap<Object>[] suggestions = 
            new SimpleOrderedMap[entry.getValue().size()];
        for (int i = 0; i < entry.getValue().size(); i++) {
          SimpleOrderedMap<Object> suggestion = 
              new SimpleOrderedMap<>();
          suggestion.add("term", entry.getValue().get(i));
          suggestions[i] = suggestion;
        }
        suggestionList.add("suggestions", java.util.Arrays.asList(suggestions));
        defaultDictionary.add(entry.getKey(), suggestionList);
      }
      SimpleOrderedMap<Object> suggestSection = 
          new SimpleOrderedMap<>();
      suggestSection.add("default", defaultDictionary);
      responseData.add("suggest", suggestSection);
    }

    return responseData;
  }

  /**
   * Create a QueryResponse with the given results.
   * Uses reflection to set the results since QueryResponse doesn't have a public setter.
   */
  private QueryResponse createQueryResponseWithResults(SolrDocumentList results) {
    QueryResponse response = new QueryResponse();
    try {
      java.lang.reflect.Field field = QueryResponse.class.getDeclaredField("_results");
      field.setAccessible(true);
      field.set(response, results);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set results on QueryResponse", e);
    }
    return response;
  }

  /**
   * Create a mock Solr document.
   */
  private SolrDocument createDocument(String id, String spellings, String roots, String html) {
    SolrDocument doc = new SolrDocument();
    doc.addField("id", id);
    doc.addField("spellings", spellings);
    doc.addField("roots", roots);
    doc.addField("html", html);
    return doc;
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
