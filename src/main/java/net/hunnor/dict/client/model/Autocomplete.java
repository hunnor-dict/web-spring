package net.hunnor.dict.client.model;

import java.util.Set;

/**
 * Representation of an item in the predictive search list.
 */
public final class Autocomplete {

	/**
	 * A headword derived from the search term.
	 */
	private String value;

	/**
	 * Shows how the suggested headword was derived from the search term.
	 * <p>
	 * true if the search term is a prefix of the headword,
	 * false for more complex transformations, e.g. the Suggester module in Solr
	 */
	private boolean prefix;

	/**
	 * The languages the headword appears in.
	 */
	private Set<Language> languages;

	/**
	 * Return the headword. 
	 * @return the headword
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the headword.
	 * @param v the headword to set.
	 */
	public void setValue(final String v) {
		this.value = v;
	}

	/**
	 * Return if the headword is a prefix of the search term.
	 * @return true for prefixes, false otherwise
	 */
	public boolean isPrefix() {
		return prefix;
	}

	/**
	 * Set if the headword is a prefix of the search term.
	 * @param s true for prefixes, false otherwise
	 */
	public void setPrefix(final boolean s) {
		this.prefix = s;
	}

	/**
	 * Return the languages the headword exists in.
	 * @return the languages the headword exists in
	 */
	public Set<Language> getLanguages() {
		return languages;
	}

	/**
	 * Set the languages the headword exists in.
	 * @param l the languages to set
	 */
	public void setLanguages(final Set<Language> l) {
		this.languages = l;
	}

}
