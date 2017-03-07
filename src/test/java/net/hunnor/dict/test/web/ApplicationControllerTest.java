package net.hunnor.dict.test.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import net.hunnor.dict.model.Contrib;
import net.hunnor.dict.model.Language;
import net.hunnor.dict.model.Response;
import net.hunnor.dict.model.Result;
import net.hunnor.dict.service.CaptchaService;
import net.hunnor.dict.service.MailerService;
import net.hunnor.dict.service.SearchService;
import net.hunnor.dict.web.ApplicationController;

/**
 * Tests for the default controller.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationController.class)
public final class ApplicationControllerTest {

	/**
	 * MVC mock object.
	 */
	@Autowired
	private MockMvc mvc;

	/**
	 * Mock object for SearchService.
	 */
	@MockBean
	private SearchService searchService;

	/**
	 * Mock object for CaptchaService.
	 */
	@MockBean
	private CaptchaService captchaService;

	/**
	 * Mock object for MailerService.
	 */
	@MockBean
	private MailerService mailerService;

	/**
	 * Test for the About controller.
	 * @throws Exception 
	 */
	@Test
	public void testAbout() throws Exception {

		Map<Language, Long> counts = new HashMap<>();
		counts.put(Language.HU, new Long(1));
		counts.put(Language.NB, new Long(1));

		given(searchService.counts()).willReturn(counts);

		mvc.perform(get("/about"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("hu", new Long(1)))
				.andExpect(model().attribute("nb", new Long(1)))
				.andExpect(view().name("views/about/index"));

	}

	/**
	 * Test for the Contrib controller.
	 * @throws Exception 
	 */
	@Test
	public void testContribFormDisplay() throws Exception {

		Contrib contrib = new Contrib();

		mvc.perform(get("/contrib")
				.param("spelling", contrib.getSpelling())
				.param("infl", contrib.getInfl())
				.param("trans", contrib.getTrans())
				.param("comments", contrib.getComments()))
				.andExpect(status().isOk())
				.andExpect(model().attribute("hasCaptcha", true))
				.andExpect(model().attribute("hasInput", false))
				.andExpect(view().name("views/contrib/index"));
	}

	/**
	 * Test for the Contrib controller.
	 * @throws Exception 
	 */
	@Test
	public void testContribFormSubmit() throws Exception {

		Contrib contrib = new Contrib();
		contrib.setSpelling("foo");

		given(captchaService.isResponseValid("", null)).willReturn(true);

		mvc.perform(post("/contrib")
				.param("spelling", contrib.getSpelling())
				.param("infl", contrib.getInfl())
				.param("trans", contrib.getTrans())
				.param("comments", contrib.getComments()))
				.andExpect(status().isOk())
				.andExpect(model().attribute("hasCaptcha", true))
				.andExpect(model().attribute("hasInput", true))
				.andExpect(model().attribute("captchaValid", true))
				.andExpect(model().attribute("messageSent", true))
				.andExpect(view().name("views/contrib/index"));

	}

	/**
	 * Test for the Download controller.
	 * @throws Exception 
	 */
	@Test
	public void testDownload() throws Exception {

		mvc.perform(get("/download"))
				.andExpect(status().isOk())
				.andExpect(view().name("views/download/index"));

	}

	/**
	 * Test for the Search controller.
	 * @throws Exception 
	 */
	@Test
	public void testSearchFormDisplay() throws Exception {

		mvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(model().attributeDoesNotExist("term", "match"))
				.andExpect(model().attributeDoesNotExist("hu", "nb"))
				.andExpect(model().attributeDoesNotExist("response"))
				.andExpect(view().name("views/search/index"));

	}

	/**
	 * Test for the Search controller.
	 * @throws Exception 
	 */
	@Test
	public void testSearchFormSubmit() throws Exception {

		Map<Language, Response> responses = new HashMap<>();
		Response hu = new Response();
		hu.setResults(new HashSet<Result>());
		hu.setSuggestions(new HashSet<String>());
		Response nb = new Response();
		nb.setResults(new HashSet<Result>());
		nb.setSuggestions(new HashSet<String>());
		responses.put(Language.HU, hu);
		responses.put(Language.NB, nb);

		given(searchService.search("foo", null)).willReturn(responses);

		mvc.perform(get("/").param("term", "foo"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("term", "foo"))
				.andExpect(model().attributeDoesNotExist("match"))
				.andExpect(model().attribute("hu", Language.HU))
				.andExpect(model().attribute("nb", Language.NB))
				.andExpect(view().name("views/search/index"));

	}

}
