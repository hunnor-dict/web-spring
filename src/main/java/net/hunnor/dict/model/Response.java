package net.hunnor.dict.model;

import java.util.Set;

/**
 * Representation of a response to a search.
 * <p>
 * Contains a set of results and a set of suggestions.
 */
public final class Response {

	/**
	 * Search results for the query.
	 */
	private Set<Result> results;

	/**
	 * Search suggestions based on the query.
	 */
	private Set<String> suggestions;

	/**
	 * Return the set of results.
	 * @return the set of results
	 */
	public Set<Result> getResults() {
		return results;
	}

	/**
	 * Set results.
	 * @param r the results to set
	 */
	public void setResults(final Set<Result> r) {
		this.results = r;
	}

	/**
	 * Return the set of suggestions.
	 * @return the set of suggestions
	 */
	public Set<String> getSuggestions() {
		return suggestions;
	}

	/**
	 * Set suggestions.
	 * @param s the suggestions to set
	 */
	public void setSuggestions(final Set<String> s) {
		this.suggestions = s;
	}

}
