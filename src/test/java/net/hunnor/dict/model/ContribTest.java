package net.hunnor.dict.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for the Contrib model class.
 */
public class ContribTest {

	@Test
	public void hasNoInputIfEmpty() {
		Contrib contrib = new Contrib();
		assertFalse(contrib.hasInput());
	}

	@Test
	public void hasContentIfHasSpelling() {
		Contrib contrib = new Contrib();
		contrib.setSpelling("foo");
		assertTrue(contrib.hasInput());
	}

	@Test
	public void hasContentIfHasInfl() {
		Contrib contrib = new Contrib();
		contrib.setInfl("foo");
		assertTrue(contrib.hasInput());
	}

	@Test
	public void hasContentIfHasTrans() {
		Contrib contrib = new Contrib();
		contrib.setTrans("foo");
		assertTrue(contrib.hasInput());
	}

	@Test
	public void hasContentIfHasComments() {
		Contrib contrib = new Contrib();
		contrib.setComments("foo");
		assertTrue(contrib.hasInput());
	}

}
