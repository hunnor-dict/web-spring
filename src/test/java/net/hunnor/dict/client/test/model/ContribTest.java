package net.hunnor.dict.client.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.hunnor.dict.client.model.Contrib;

/**
 * Tests for the Contrib model class.
 */
public final class ContribTest {

	/**
	 * The constructor with all fields should set all fields correctly.
	 */
	@Test
	public void constructorWithAllFields() {
		Contrib contrib = new Contrib("foo", "bar", "baz", "qux");
		assertEquals("foo", contrib.getSpelling());
		assertEquals("bar", contrib.getInfl());
		assertEquals("baz", contrib.getTrans());
		assertEquals("qux", contrib.getComments());
	}

	/**
	 * A Contrib object should be empty by default.
	 */
	@Test
	public void hasNoInputIfEmpty() {
		Contrib contrib = new Contrib();
		assertFalse(contrib.hasInput());
	}

	/**
	 * If spelling is defined, Contrib is not empty.
	 */
	@Test
	public void hasContentIfHasSpelling() {
		Contrib contrib = new Contrib();
		contrib.setSpelling("foo");
		assertTrue(contrib.hasInput());
	}

	/**
	 * If infl is defined, Contrib is not empty.
	 */
	@Test
	public void hasContentIfHasInfl() {
		Contrib contrib = new Contrib();
		contrib.setInfl("foo");
		assertTrue(contrib.hasInput());
	}

	/**
	 * If trans is defined, Contrib is not empty.
	 */
	@Test
	public void hasContentIfHasTrans() {
		Contrib contrib = new Contrib();
		contrib.setTrans("foo");
		assertTrue(contrib.hasInput());
	}

	/**
	 * If comment is defined, Contrib is not empty.
	 */
	@Test
	public void hasContentIfHasComments() {
		Contrib contrib = new Contrib();
		contrib.setComments("foo");
		assertTrue(contrib.hasInput());
	}

	/**
	 * If all fields are empty, Contrib is empty.
	 */
	@Test
	public void hasNoContentIfAllFieldsEmpty() {
		Contrib contrib = new Contrib();
		contrib.setSpelling("");
		contrib.setInfl("");
		contrib.setTrans("");
		contrib.setComments("");
		assertFalse(contrib.hasInput());
	}

}
