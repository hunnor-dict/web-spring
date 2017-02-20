package net.hunnor.dict.service.impl;

import java.io.IOException;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.hunnor.dict.model.Autocomplete;
import net.hunnor.dict.model.Language;
import net.hunnor.dict.model.Response;
import net.hunnor.dict.model.Result;
import net.hunnor.dict.model.SearchException;
import net.hunnor.dict.service.SearchService;

/**
 * SearchService implementation with Solr.
 */
@Service
public final class SolrSearchService implements SearchService {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(SearchService.class);

	/**
	 * Logger for search terms.
	 */
	private static final Logger SEARCH_LOGGER =
			LoggerFactory.getLogger("net.hunnor.dict.log.searches");

	/**
	 * The URL of the Solr server.
	 */
	@Value("${net.hunnor.dict.search.solr.url}")
	private String baseURL;

	/**
	 * Constants prefix for core names.
	 */
	@Value("${net.hunnor.dict.search.solr.core.names.prefix}")
	private String coreNamesPrefix;

	/**
	 * The names of the Solr cores to search.
	 */
	@Value("${net.hunnor.dict.search.solr.cores}")
	private String[] cores;

	/**
	 * The maximum number of results to return.
	 */
	@Value("${net.hunnor.dict.search.solr.max.count}")
	private int maxCount;

	/**
	 * The maximum number of suggestions to return.
	 */
	@Value("${net.hunnor.dict.search.solr.suggestions.max.count}")
	private int suggestionsMaxCount;

	/**
	 * The maximum length of a term for which suggestions are returned.
	 */
	@Value("${net.hunnor.dict.search.solr.suggestions.max.length}")
	private int suggestionsMaxLength;

	/**
	 * Collation rules for Hungarian and Norwegian terms.
	 */
	@Value("${net.hunnor.dict.search.collation.rules}")
	private String collationRules;

	/**
	 * The object that manages connections to the Solr server.
	 */
	private SolrClient solrClient;

	/**
	 * Collator object for Hungarian and Norwegian terms.
	 */
	private Collator collator;

	/**
	 * Connect to the Solr server after the service is created.
	 */
	@PostConstruct
	private void setUpSolrClient() {
		solrClient = new HttpSolrClient.Builder(baseURL).build();
	}

	/**
	 * Set up collator after the service is created.
	 */
	@PostConstruct
	private void setUpCollator() {
		try {
			collator = new RuleBasedCollator(collationRules);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public Map<Language, Long> counts() throws SearchException {

		Map<Language, Long> counts = new TreeMap<>();

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set(CommonParams.Q, "*:*");
		solrQuery.set(CommonParams.OMIT_HEADER, true);
		solrQuery.set(CommonParams.ROWS, 0);

		for (Language language: Language.values()) {
			try {
				QueryResponse queryResponse = solrClient.query(
						(coreNamesPrefix + language)
								.toLowerCase(Locale.ENGLISH),
						solrQuery);
				SolrDocumentList solrDocumentList = queryResponse.getResults();
				counts.put(language, solrDocumentList.getNumFound());
			} catch (SolrServerException | IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new SearchException();
			}
		}

		return counts;

	}

	@Override
	public Map<Language, Response> search(
			final String term,
			final String match) throws SearchException {

		SolrQuery baseQuery = new SolrQuery();
		baseQuery.set(CommonParams.FIELD, "id,html");
		baseQuery.set(HighlightParams.HIGHLIGHT, true);
		baseQuery.set(HighlightParams.FIELDS, "html");
		baseQuery.set(CommonParams.OMIT_HEADER, true);
		baseQuery.set(CommonParams.ROWS, maxCount);
		baseQuery.set(CommonParams.SORT, "sort asc");

		String escapedTerm = ClientUtils.escapeQueryChars(term);
		if (escapedTerm.contains(" ")) {
			escapedTerm = "\"" + escapedTerm + "\"~2";
		}

		Map<Language, Response> responses = new TreeMap<>();
		for (Language language: Language.values()) {
			Response response = new Response();
			Set<String> suggestions = new TreeSet<>(collator);
			response.setSuggestions(suggestions);
			Set<Result> results = new LinkedHashSet<>();
			response.setResults(results);
			responses.put(language, response);
		}

		boolean hasResults;

		// Phase 1
		SolrQuery p1Query = baseQuery.getCopy();
		p1Query.set(CommonParams.Q, "roots:" + escapedTerm);
		hasResults = searchPhase(responses, p1Query, true);

		// Phase 2
		if (!hasResults || "forms".equals(match) || "full".equals(match)) {
			SolrQuery p2Query = baseQuery.getCopy();
			p2Query.set(CommonParams.Q, "forms:" + escapedTerm);
			hasResults = searchPhase(responses, p2Query, true);
		}

		if (!hasResults || "full".equals(match)) {
			SolrQuery p3Query = baseQuery.getCopy();
			p3Query.set(CommonParams.Q, "trans:" + escapedTerm
					+ " egTrans:" + escapedTerm);
			hasResults = searchPhase(responses, p3Query, true);
		}

		// Log search
		StringBuilder sb = new StringBuilder();
		if (hasResults) {
			sb.append("1");
		} else {
			sb.append("0");
		}
		sb.append("|").append(term);
		SEARCH_LOGGER.info(sb.toString());

		return responses;

	}

	/**
	 * Execute the search on a set of index fields.
	 * @param responses the response objects to add results to
	 * @param solrQuery the Solr query to execute
	 * @param spellCheck if spell checking should be performed
	 * @return true if the phase produced results, false otherwise
	 * @throws SearchException if the search fails
	 */
	private boolean searchPhase(
			final Map<Language, Response> responses,
			final SolrQuery solrQuery,
			final boolean spellCheck) throws SearchException {

		boolean hasResults = false;

		for (Language language: Language.values()) {
			Response response = responses.get(language);

			try {

				QueryResponse queryResponse = solrClient.query(
						(coreNamesPrefix + language)
								.toLowerCase(Locale.ENGLISH),
						solrQuery);
				SolrDocumentList solrDocumentList = queryResponse.getResults();
				long numFound = solrDocumentList.getNumFound();

				if (numFound == 0 && spellCheck) {

					getSuggestions(queryResponse, response);

				} else {

					hasResults = true;

					getResults(queryResponse, response);

				}

			} catch (SolrServerException | IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new SearchException();
			}

		}

		return hasResults;

	}

	/**
	 * Add spell checker results to the response.
	 * @param queryResponse the query response with the spell checker results
	 * @param response the response
	 */
	private void getSuggestions(
			final QueryResponse queryResponse, final Response response) {
		SpellCheckResponse spellCheckResponse =
				queryResponse.getSpellCheckResponse();
		if (spellCheckResponse != null) {
			List<Suggestion> suggestionList =
					spellCheckResponse.getSuggestions();
			for (Suggestion suggestion: suggestionList) {
				List<String> alternatives =
						suggestion.getAlternatives();
				for (String alternative: alternatives) {
					response.getSuggestions().add(alternative);
				}
			}
		}
	}

	/**
	 * Add results to the response.
	 * @param queryResponse the query response with the results
	 * @param response the response
	 */
	private void getResults(
			final QueryResponse queryResponse,
			final Response response) {
		Set<Result> resultSet = response.getResults();
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		Map<String, Map<String, List<String>>> highlighting =
				queryResponse.getHighlighting();
		for (SolrDocument solrDocument: solrDocumentList) {
			Result result = getResultFromDocument(solrDocument, highlighting);
			resultSet.add(result);
		}
	}

	/**
	 * Build a result from a Solr document, or optionally
	 * from the set of highlighted content.
	 * @param solrDocument the Solr document
	 * @param highlighting the set of highlighted content
	 * @return the result with data from the Solr document
	 */
	private Result getResultFromDocument(
			final SolrDocument solrDocument,
			final Map<String, Map<String, List<String>>> highlighting) {
		String id = solrDocument.getFieldValue("id").toString();
		String html = solrDocument
				.getFieldValue("html").toString();
		if (highlighting != null) {
			Map<String, List<String>> highlightingMap =
					highlighting.get(id);
			if (highlightingMap != null) {
				List<String> fragments =
						highlightingMap.get("html");
				if (fragments != null
						&& fragments.size() == 1) {
					html = fragments.get(0);
				}
			}
		}
		return new Result(id, html);
	}

	@Override
	public List<Autocomplete> suggest(
			final String searchTerm) throws SearchException {

		Map<String, Autocomplete> suggestions =
				new TreeMap<>(collator);

		if (searchTerm != null && searchTerm.length() <= suggestionsMaxLength) {

			Map<Language, QueryResponse> responses =
					getSuggestions(searchTerm);

			for (Language language: Language.values()) {
				getTermsFromResponse(
						language, responses.get(language), suggestions);
			}
			if (suggestions.isEmpty()) {
				for (Language language: Language.values()) {
					getSuggestionsFromResponse(
							language, responses.get(language), suggestions);
				}
			}

		}

		return new ArrayList<>(suggestions.values());

	}

	/**
	 * Return suggestions for a search term for each language.
	 * @param term the term to return suggestions for
	 * @return suggestions for each language
	 * @throws SearchException if the search fails
	 */
	private Map<Language, QueryResponse> getSuggestions(
			final String term) throws SearchException {
		Map<Language, QueryResponse> responses = new EnumMap<>(Language.class);
		String q = ClientUtils.escapeQueryChars(term);
		for (Language language: Language.values()) {

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRequestHandler("/suggest");
			solrQuery.set(CommonParams.Q, q);
			solrQuery.set(CommonParams.OMIT_HEADER, true);
			solrQuery.set("suggest.count", suggestionsMaxCount);
			solrQuery.set(TermsParams.TERMS_PREFIX_STR, q);
			try {
				QueryResponse response = solrClient.query(
								(coreNamesPrefix + language)
										.toLowerCase(Locale.ENGLISH),
								solrQuery);
				responses.put(language, response);
			} catch (SolrServerException | IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new SearchException();
			}
		}
		return responses;
	}

	/**
	 * Extract suggestions coming from the Term component.
	 * @param language the language terms are processed for
	 * @param queryResponse the response object from Solr
	 * @param suggestions the suggestion list to add terms to
	 */
	private void getTermsFromResponse(
			final Language language,
			final QueryResponse queryResponse,
			final Map<String, Autocomplete> suggestions) {
		TermsResponse termsResponse = queryResponse.getTermsResponse();
		List<Term> terms = termsResponse.getTerms("spellings");
		for (Term term: terms) {
			String t = term.getTerm();
			Autocomplete suggestion = suggestions.get(t);
			if (suggestion == null) {
				suggestion = new Autocomplete();
				suggestion.setValue(t);
				suggestion.setPrefix(true);
				Set<Language> languages = new TreeSet<>();
				languages.add(language);
				suggestion.setLanguages(languages);
				suggestions.put(t, suggestion);
			} else {
				Set<Language> languages = suggestion.getLanguages();
				languages.add(language);
			}
		}
	}

	/**
	 * Extract suggestions coming from the Suggester component.
	 * @param language the language terms are processed for
	 * @param queryResponse the response object from Solr
	 * @param suggestions the suggestion list to add terms to
	 */
	private void getSuggestionsFromResponse(
			final Language language,
			final QueryResponse queryResponse,
			final Map<String, Autocomplete> suggestions) {
		SuggesterResponse suggesterResponse =
				queryResponse.getSuggesterResponse();
		Map<String, List<String>> suggestionMap =
				suggesterResponse.getSuggestedTerms();
		for (Entry<String, List<String>> suggestionEntry
				: suggestionMap.entrySet()) {
			List<String> suggestionList = suggestionEntry.getValue();
			for (String suggestedTerm: suggestionList) {
				Autocomplete suggestion = suggestions.get(suggestedTerm);
				if (suggestion == null) {
					suggestion = new Autocomplete();
					suggestion.setValue(suggestedTerm);
					suggestion.setPrefix(false);
					Set<Language> languages = new TreeSet<>();
					languages.add(language);
					suggestion.setLanguages(languages);
					suggestions.put(suggestedTerm, suggestion);
				} else {
					Set<Language> languages = suggestion.getLanguages();
					languages.add(language);
				}
			}
		}
	}

}
