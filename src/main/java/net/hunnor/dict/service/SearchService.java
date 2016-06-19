package net.hunnor.dict.service;

import java.util.Map;

import java.util.List;

import net.hunnor.dict.model.Autocomplete;
import net.hunnor.dict.model.Language;
import net.hunnor.dict.model.Response;
import net.hunnor.dict.model.SearchException;

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
