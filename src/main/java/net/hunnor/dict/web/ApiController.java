package net.hunnor.dict.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.hunnor.dict.model.Autocomplete;
import net.hunnor.dict.model.SearchException;
import net.hunnor.dict.service.SearchService;

/**
 * Controller for machine-readable interfaces.
 */
@Controller
public final class ApiController {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(ApiController.class);

	/**
	 * The SearchService to use for searches.
	 */
	@Autowired
	private SearchService searchService;

	/**
	 * Controller method for search suggestions (jQuery autocomplete).
	 * @param term the term to return suggestions for
	 * @return search suggestions in JSON
	 */
	@RequestMapping(
			value = "/suggest",
			method = RequestMethod.GET,
			produces = {"application/json"})
	@ResponseBody
	public List<Autocomplete> suggest(
			@RequestParam(value = "term", required = false) final String term) {
		List<Autocomplete> result = null;
		try {
			result = searchService.suggest(term);
		} catch (SearchException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Controller method for search suggestion (OpenSearch).
	 * @param term the term to return suggestions for
	 * @return search suggestions in JSON
	 */
	@RequestMapping(
			value = "/opensearch/suggest",
			method = RequestMethod.GET,
			produces = {"application/x-suggestions+json"})
	@ResponseBody
	public List<Object> opensearchSuggest(
			@RequestParam(value = "term", required = false) final String term) {

		List<Autocomplete> autocompletes = null;
		try {
			autocompletes = searchService.suggest(term);
		} catch (SearchException e) {
			LOGGER.error(e.getMessage(), e);
		}

		List<Object> results = new ArrayList<>();
		results.add(term);

		List<String> suggestions = new ArrayList<>();
		for (Autocomplete autocomplete: autocompletes) {
			suggestions.add(autocomplete.getValue());
		}
		results.add(suggestions);

		return results;

	}

}
