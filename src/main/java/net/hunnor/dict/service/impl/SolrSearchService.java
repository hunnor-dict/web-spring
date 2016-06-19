package net.hunnor.dict.service.impl;

import java.io.IOException;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
	 * s.
	 */
	private SolrClient solrClient;

	/**
	 * Collation rules for Hungarian and Norwegian terms.
	 */
	private static final String COLLATION_HU_NO = 
			"< a, A, á, Á"
			+ "< b, B"
			+ "< c, C"
			+ "< d, D"
			+ "< e, E, é, É"
			+ "< f, F"
			+ "< g, G"
			+ "< h, H"
			+ "< i, I, í, Í"
			+ "< j, J"
			+ "< k, K"
			+ "< l, L"
			+ "< m, M"
			+ "< n, N"
			+ "< o, O, ó, Ó"
			+ "< ö, Ö, ő, Ő"
			+ "< p, P"
			+ "< q, Q"
			+ "< r, R"
			+ "< s, S"
			+ "< t, T"
			+ "< u, U, ú, Ú"
			+ "< ü, Ü, ű, Ű"
			+ "< v, V"
			+ "< w, W"
			+ "< x, X"
			+ "< y, Y"
			+ "< z, Z"
			+ "< æ, Æ"
			+ "< ø, Ø"
			+ "< å, Å";

	/**
	 * Collator object for Hungarian and Norwegian terms.
	 */
	private static Collator collator = null;

	static {
		try {
			collator = new RuleBasedCollator(COLLATION_HU_NO);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Connect to the Solr server after the service is created.
	 */
	@PostConstruct
	private void connect() {
		solrClient = new HttpSolrClient.Builder(baseURL).build();
	}

	@Override
	public Map<Language, Long> counts() throws SearchException {

		Map<Language, Long> counts = new TreeMap<>();

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("q", "*:*");
		solrQuery.set("omitHeader", true);
		solrQuery.set("rows", 0);

		for (Language language: Language.values()) {
			try {
				QueryResponse queryResponse =
						solrClient.query("hunnor." + language, solrQuery);
				SolrDocumentList solrDocumentList = queryResponse.getResults();
				counts.put(language, solrDocumentList.getNumFound());
			} catch (SolrServerException e) {
				LOGGER.error(e.getMessage(), e);
				throw new SearchException();
			} catch (IOException e) {
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
		baseQuery.set("fl", "id,html");
		baseQuery.set("hl", true);
		baseQuery.set("hl.fl", "html");
		baseQuery.set("omitHeader", true);
		baseQuery.set("rows", maxCount);
		baseQuery.set("sort", "sort asc");

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

		Boolean hasResults = false;

		// Phase 1
		SolrQuery p1Query = baseQuery.getCopy();
		p1Query.set("q", "roots:" + escapedTerm);
		hasResults = searchPhase(responses, p1Query, true);

		// Phase 2
		if (!hasResults || "forms".equals(match) || "full".equals(match)) {
			SolrQuery p2Query = baseQuery.getCopy();
			p2Query.set("q", "forms:" + escapedTerm);
			hasResults = searchPhase(responses, p2Query, true);
		}

		if (!hasResults || "full".equals(match)) {
			SolrQuery p3Query = baseQuery.getCopy();
			p3Query.set("q", "trans:" + escapedTerm
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
			Set<Result> resultSet = response.getResults();

			try {

				QueryResponse queryResponse =
						solrClient.query("hunnor." + language, solrQuery);
				SolrDocumentList solrDocumentList = queryResponse.getResults();
				long numFound = solrDocumentList.getNumFound();

				if (numFound == 0) {

					if (spellCheck) {
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

				} else {

					hasResults = true;

					Map<String, Map<String, List<String>>> highlighting =
							queryResponse.getHighlighting();
					for (SolrDocument solrDocument: solrDocumentList) {
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
						Result result = new Result(id, html);
						resultSet.add(result);
					}

				}

			} catch (SolrServerException e) {
				LOGGER.error(e.getMessage(), e);
				throw new SearchException();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new SearchException();
			}

		}

		return hasResults;

	}

	@Override
	public List<Autocomplete> suggest(
			final String searchTerm) throws SearchException {

		Map<String, Autocomplete> suggestions =
				new TreeMap<String, Autocomplete>(collator);

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

		return new ArrayList<Autocomplete>(suggestions.values());

	}

	/**
	 * Return suggestions for a search term for each language.
	 * @param term the term to return suggestions for
	 * @return suggestions for each language
	 * @throws SearchException if the search fails
	 */
	private Map<Language, QueryResponse> getSuggestions(
			final String term) throws SearchException {
		Map<Language, QueryResponse> responses = new HashMap<>();
		String q = ClientUtils.escapeQueryChars(term);
		for (Language language: Language.values()) {

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRequestHandler("/suggest");
			solrQuery.set("q", q);
			solrQuery.set("omitHeader", true);
			solrQuery.set("suggest.count", suggestionsMaxCount);
			solrQuery.set("terms.prefix", q);
			try {
				QueryResponse response =
						solrClient.query("hunnor." + language, solrQuery);
				responses.put(language, response);
			} catch (SolrServerException e) {
				LOGGER.error(e.getMessage(), e);
				throw new SearchException();
			} catch (IOException e) {
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
				Set<Language> languages = new TreeSet<Language>();
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
					Set<Language> languages = new TreeSet<Language>();
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
