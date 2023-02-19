package net.hunnor.dict.client.service.impl;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.hunnor.dict.client.model.Autocomplete;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.Response;
import net.hunnor.dict.client.model.Result;
import net.hunnor.dict.client.service.SearchService;
import net.hunnor.dict.client.service.ServiceException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.HighlightParams;
import org.apache.solr.common.params.TermsParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SolrSearchService implements SearchService {

  private static final Logger searches =
      LoggerFactory.getLogger("net.hunnor.dict.client.log.searches");

  @Value("${net.hunnor.dict.client.search.solr.core.names.prefix}")
  private String coreNamesPrefix;

  @Value("${net.hunnor.dict.client.search.solr.cores}")
  private String[] cores;

  @Value("${net.hunnor.dict.client.search.solr.max.count}")
  private int maxCount;

  @Value("${net.hunnor.dict.client.search.solr.suggestions.max.count}")
  private int suggestionsMaxCount;

  @Value("${net.hunnor.dict.client.search.solr.suggestions.max.length}")
  private int suggestionsMaxLength;

  @Autowired
  private SolrClient solrClient;

  @Autowired
  private Collator collator;

  @Override
  public Map<Language, Long> counts() throws ServiceException {
    Map<Language, Long> counts = new EnumMap<>(Language.class);
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.set(CommonParams.Q, "*:*");
    solrQuery.set(CommonParams.OMIT_HEADER, true);
    solrQuery.set(CommonParams.ROWS, 0);
    for (Language language: Language.values()) {
      try {
        QueryResponse queryResponse = solrClient.query(
            (coreNamesPrefix + language).toLowerCase(Locale.ENGLISH), solrQuery);
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        counts.put(language, solrDocumentList.getNumFound());
      } catch (SolrServerException | IOException ex) {
        throw new ServiceException(ex.getMessage(), ex);
      }
    }
    return counts;
  }

  @Override
  public Map<Language, Response> search(String term, String match) throws ServiceException {
    SolrQuery baseQuery = new SolrQuery();
    baseQuery.set(CommonParams.FIELD, "id,html");
    baseQuery.set(HighlightParams.HIGHLIGHT, true);
    baseQuery.set(HighlightParams.FIELDS, "html");
    baseQuery.set(CommonParams.OMIT_HEADER, true);
    baseQuery.set(CommonParams.ROWS, maxCount);
    baseQuery.set(CommonParams.SORT, "sort asc");

    String escapedTerm = ClientUtils.escapeQueryChars(term.trim());
    if (escapedTerm.contains(" ")) {
      escapedTerm = "\"" + escapedTerm + "\"~2";
    }

    Map<Language, Response> responses = new TreeMap<>();
    for (Language language: Language.values()) {
      Response response = new Response(collator);
      responses.put(language, response);
    }

    boolean hasResults;

    // Phase 1
    SolrQuery p1Query = baseQuery.getCopy();
    p1Query.set(CommonParams.Q, "roots:" + escapedTerm);
    hasResults = searchPhase(responses, p1Query);

    // Phase 2
    if (!hasResults || "forms".equals(match) || "full".equals(match)) {
      SolrQuery p2Query = baseQuery.getCopy();
      p2Query.set(CommonParams.Q, "forms:" + escapedTerm);
      hasResults = searchPhase(responses, p2Query);
    }

    if (!hasResults || "full".equals(match)) {
      SolrQuery p3Query = baseQuery.getCopy();
      p3Query.set(CommonParams.Q, "trans:" + escapedTerm
          + " egTrans:" + escapedTerm);
      hasResults = searchPhase(responses, p3Query);
    }

    // Log search
    if (searches.isInfoEnabled()) {
      StringBuilder sb = new StringBuilder()
          .append(hasResults ? "1" : "0")
          .append("|")
          .append(term.replaceAll("[\\r\\n\\t]", ""));
      searches.info(sb.toString());
    }

    return responses;

  }

  private boolean searchPhase(
      Map<Language, Response> responses,
      SolrQuery solrQuery) throws ServiceException {
    boolean hasResults = false;
    for (Language language: Language.values()) {
      Response response = responses.get(language);
      try {
        String coreName = (coreNamesPrefix + language).toLowerCase(Locale.ENGLISH);
        QueryResponse queryResponse = solrClient.query(coreName, solrQuery);
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        long numFound = solrDocumentList.getNumFound();
        if (numFound == 0) {
          getSpellingSuggestions(queryResponse, response);
        } else {
          hasResults = true;
          getResults(queryResponse, response);
        }
      } catch (SolrServerException | IOException ex) {
        throw new ServiceException(ex.getMessage(), ex);
      }
    }

    return hasResults;

  }

  private void getSpellingSuggestions(QueryResponse queryResponse, Response response) {
    SpellCheckResponse spellCheckResponse = queryResponse.getSpellCheckResponse();
    if (spellCheckResponse != null) {
      List<Suggestion> suggestionList = spellCheckResponse.getSuggestions();
      for (Suggestion suggestion: suggestionList) {
        List<String> alternatives = suggestion.getAlternatives();
        for (String alternative: alternatives) {
          response.addSuggestion(alternative);
        }
      }
    }
  }

  private void getResults(QueryResponse queryResponse, Response response) {
    SolrDocumentList solrDocumentList = queryResponse.getResults();
    Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
    for (SolrDocument solrDocument: solrDocumentList) {
      Result result = getResultFromDocument(solrDocument, highlighting);
      response.addResult(result);
    }
  }

  private Result getResultFromDocument(
      SolrDocument solrDocument,
      Map<String, Map<String, List<String>>> highlighting) {
    String id = solrDocument.getFieldValue("id").toString();
    String html = solrDocument.getFieldValue("html").toString();
    if (highlighting != null) {
      Map<String, List<String>> highlightingMap = highlighting.get(id);
      List<String> fragments = highlightingMap.get("html");
      if (fragments != null) {
        html = fragments.get(0);
      }
    }
    return new Result(id, html);
  }

  @Override
  public List<Autocomplete> suggest(String term) throws ServiceException {
    Map<String, Autocomplete> suggestions = new TreeMap<>(collator);
    if (term != null && term.length() <= suggestionsMaxLength) {
      Map<Language, QueryResponse> responses = getSuggestions(term);
      for (Language language: Language.values()) {
        getTermsFromResponse(language, responses.get(language), suggestions);
      }
      if (suggestions.isEmpty()) {
        for (Language language: Language.values()) {
          getSuggestionsFromResponse(language, responses.get(language), suggestions);
        }
      }
    }
    return new ArrayList<>(suggestions.values());
  }

  private Map<Language, QueryResponse> getSuggestions(String term) throws ServiceException {
    Map<Language, QueryResponse> responses = new EnumMap<>(Language.class);
    String escapedTerm = ClientUtils.escapeQueryChars(term.trim());
    for (Language language: Language.values()) {
      SolrQuery solrQuery = new SolrQuery();
      solrQuery.setRequestHandler("/suggest");
      solrQuery.set(CommonParams.Q, escapedTerm);
      solrQuery.set(CommonParams.OMIT_HEADER, true);
      solrQuery.set("suggest.count", suggestionsMaxCount);
      solrQuery.set(TermsParams.TERMS_PREFIX_STR, escapedTerm);
      try {
        QueryResponse response = solrClient.query(
            (coreNamesPrefix + language).toLowerCase(Locale.ENGLISH), solrQuery);
        responses.put(language, response);
      } catch (SolrServerException | IOException ex) {
        throw new ServiceException(ex.getMessage(), ex);
      }
    }
    return responses;
  }

  private void getTermsFromResponse(
      Language language, QueryResponse queryResponse, Map<String, Autocomplete> suggestions) {
    TermsResponse termsResponse = queryResponse.getTermsResponse();
    List<Term> terms = termsResponse.getTerms("spellings");
    for (Term term: terms) {
      String text = term.getTerm();
      Autocomplete suggestion = suggestions.get(text);
      if (suggestion == null) {
        suggestion = new Autocomplete();
        suggestion.setValue(text);
        suggestion.setPrefix(true);
        suggestion.addLanguage(language);
        suggestions.put(text, suggestion);
      } else {
        suggestion.addLanguage(language);
      }
    }
  }

  private void getSuggestionsFromResponse(
      Language language, QueryResponse queryResponse, Map<String, Autocomplete> suggestions) {
    SuggesterResponse suggesterResponse = queryResponse.getSuggesterResponse();
    Map<String, List<String>> suggestionMap = suggesterResponse.getSuggestedTerms();
    for (Entry<String, List<String>> suggestionEntry: suggestionMap.entrySet()) {
      List<String> suggestionList = suggestionEntry.getValue();
      for (String suggestedTerm: suggestionList) {
        Autocomplete suggestion = suggestions.get(suggestedTerm);
        if (suggestion == null) {
          suggestion = new Autocomplete();
          suggestion.setValue(suggestedTerm);
          suggestion.setPrefix(false);
          suggestion.addLanguage(language);
          suggestions.put(suggestedTerm, suggestion);
        } else {
          suggestion.addLanguage(language);
        }
      }
    }
  }

}
