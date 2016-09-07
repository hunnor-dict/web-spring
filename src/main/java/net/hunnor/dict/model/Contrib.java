package net.hunnor.dict.model;

/**
 * Representation of a suggestion sent by the users.
 */
public final class Contrib {

	/**
	 * The standard spelling of the word.
	 */
	private String spelling;

	/**
	 * Inflected forms of the word.
	 */
	private String infl;

	/**
	 * Definition or equivalents of the word.
	 */
	private String trans;

	/**
	 * Additional comments.
	 */
	private String comments;

	/**
	 * Default constructor.
	 */
	public Contrib() {
		super();
	}

	/**
	 * Constructor with all fields.
	 * @param s the spelling of the new instance
	 * @param i the inflection of the new instance
	 * @param t the translation of the new instance
	 * @param c the comments of the new instance
	 */
	public Contrib(
			final String s,
			final String i,
			final String t,
			final String c) {
		super();
		this.spelling = s;
		this.infl = i;
		this.trans = t;
		this.comments = c;
	}

	/**
	 * Return the spelling of the word.
	 * @return the spelling of the word
	 */
	public String getSpelling() {
		return spelling;
	}

	/**
	 * Set the spelling of the word.
	 * @param s the spelling to set
	 */
	public void setSpelling(final String s) {
		this.spelling = s;
	}

	/**
	 * Return the inflection of the word.
	 * @return the inflection of the word
	 */
	public String getInfl() {
		return infl;
	}

	/**
	 * Set the inflection of the word.
	 * @param i the inflection to set
	 */
	public void setInfl(final String i) {
		this.infl = i;
	}

	/**
	 * Return the translation of the word.
	 * @return the translation of the word
	 */
	public String getTrans() {
		return trans;
	}

	/**
	 * Set the translation of the word.
	 * @param t the translation to set
	 */
	public void setTrans(final String t) {
		this.trans = t;
	}

	/**
	 * Return comments for the word.
	 * @return comments for the word
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Set comments for the word.
	 * @param c the comments to set
	 */
	public void setComments(final String c) {
		this.comments = c;
	}

	/**
	 * Check if any of the fields have data.
	 * @return true if any of the fields is a not empty string, false otherwise
	 */
	public boolean hasInput() {
		if (spelling != null && !spelling.isEmpty()) {
			return true;
		}
		if (infl != null && !infl.isEmpty()) {
			return true;
		}
		if (trans != null && !trans.isEmpty()) {
			return true;
		}
		if (comments != null && !comments.isEmpty()) {
			return true;
		}
		return false;
	}

}
