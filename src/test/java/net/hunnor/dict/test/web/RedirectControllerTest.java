package net.hunnor.dict.test.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import net.hunnor.dict.web.RedirectController;

/**
 * Tests for redirection from URLs used in previous versions.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(RedirectController.class)
public final class RedirectControllerTest {

	/**
	 * MVC mock object.
	 */
	@Autowired
	private MockMvc mvc;

	/**
	 * Test redirection from previous mobile pages.
	 * @throws Exception 
	 */
	@Test
	public void testMobile() throws Exception {
		mvc.perform(get("/m")).andExpect(status().isFound());
		mvc.perform(get("/m/about")).andExpect(status().isFound());
	}

	/**
	 * Test redirection from previous Norwegian pages.
	 * @throws Exception 
	 */
	@Test
	public void testNorwegian() throws Exception {
		mvc.perform(get("/no")).andExpect(status().isFound());
		mvc.perform(get("/no/about")).andExpect(status().isFound());
	}

	/**
	 * Test redirection from previous search page.
	 * @throws Exception 
	 */
	@Test
	public void testSearch() throws Exception {
		mvc.perform(get("/search")).andExpect(status().isFound());
	}

	/**
	 * Test redirection from previous grammar pages.
	 * @throws Exception 
	 */
	@Test
	public void testGramm() throws Exception {
		mvc.perform(get("/gramm")).andExpect(status().isFound());
		mvc.perform(get("/gramm/pron")).andExpect(status().isFound());
	}

}
