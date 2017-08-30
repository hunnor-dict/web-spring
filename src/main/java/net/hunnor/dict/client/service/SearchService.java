package net.hunnor.dict.client.service;

import java.util.Map;

import net.hunnor.dict.client.model.Autocomplete;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.Response;
import net.hunnor.dict.client.model.SearchException;

import java.util.List;

/**
 * Service for searches.
 */
public interface SearchService {

	/**
	 * Return the number of entries for each language.
	 * @return the number of entries for each language
	 * @throws SearchException if the search fails
	 */
	Map<Language, Long> counts() throws SearchException;

	/**
	 * Return search results for each language.
	 * @param term the term to search for
	 * @param match define how matching should be performed
	 * @return search results for each language
	 * @throws SearchException if the search fails
	 */
	Map<Language, Response> search(String term, String match)
			throws SearchException;

	/**
	 * Return suggestions for a term.
	 * @param term the term to give suggestions for
	 * @return suggestions for the term
	 * @throws SearchException if the search fails
	 */
	List<Autocomplete> suggest(String term) throws SearchException;

}
