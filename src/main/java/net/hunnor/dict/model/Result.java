package net.hunnor.dict.model;

/**
 * Representation of a search result.
 */
public final class Result {

	/**
	 * The ID of the result.
	 */
	private String id;

	/**
	 * The HTML content of the result.
	 */
	private String html;

	/**
	 * Default constructor.
	 */
	public Result() {
		super();
	}

	/**
	 * Constructor with field ID.
	 * @param i the ID of the new instance
	 */
	public Result(final String i) {
		this.id = i;
	}

	/**
	 * Constructor with fields ID and content.
	 * @param i the ID of the new instance
	 * @param h the content of the new instance
	 */
	public Result(final String i, final String h) {
		this.id = i;
		this.html = h;
	}

	/**
	 * Return the ID of the result.
	 * @return the ID of the result
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the ID.
	 * @param i the ID to set
	 */
	public void setId(final String i) {
		this.id = i;
	}

	/**
	 * Return the content of the result.
	 * @return the content of the result
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * Set the content.
	 * @param h the content to set
	 */
	public void setHtml(final String h) {
		this.html = h;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		int h = 0;
		if (html != null) {
			h = html.hashCode();
		}
		result = prime * result + h;
		int i = 0;
		if (id != null) {
			i = id.hashCode();
		}
		result = prime * result + i;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Result other = (Result) obj;
		if (html == null) {
			if (other.html != null) {
				return false;
			}
		} else if (!html.equals(other.html)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
