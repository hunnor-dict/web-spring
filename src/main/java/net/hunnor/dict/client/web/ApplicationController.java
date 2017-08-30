package net.hunnor.dict.client.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.hunnor.dict.client.model.CaptchaException;
import net.hunnor.dict.client.model.Contrib;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.MailerException;
import net.hunnor.dict.client.model.Response;
import net.hunnor.dict.client.model.SearchException;
import net.hunnor.dict.client.service.CaptchaService;
import net.hunnor.dict.client.service.MailerService;
import net.hunnor.dict.client.service.SearchService;

/**
 * Default controller of the application.
 */
@Controller
public final class ApplicationController {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(ApplicationController.class);

	/**
	 * Service for searches.
	 */
	@Autowired
	private SearchService searchService;

	/**
	 * Service for processing CAPTCHA requests.
	 */
	@Autowired
	private CaptchaService captchaService;

	/**
	 * Service for sending e-mails.
	 */
	@Autowired
	private MailerService mailerService;

	/**
	 * Controller method for the About page.
	 * @param model the model to pass to the view
	 * @return the name of the About view
	 */
	@RequestMapping(
			value = "/about",
			method = RequestMethod.GET)
	public String about(final Model model) {
		try {
			Map<Language, Long> counts = searchService.counts();
			model.addAttribute("hu", counts.get(Language.HU));
			model.addAttribute("nb", counts.get(Language.NB));
		} catch (SearchException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return ViewConstants.ABOUT_VIEW;
	}

	/**
	 * Controller method for the suggestions page.
	 * @param contrib the suggestion being processed
	 * @param captchaResponse response from the CAPTCHA validation service
	 * @param model the model to pass to the view
	 * @return the name of the suggestions view
	 */
	@RequestMapping(
			value = "/contrib",
			method = {RequestMethod.GET, RequestMethod.POST})
	public String contrib(
			@ModelAttribute final Contrib contrib,
			@RequestParam(value = "g-recaptcha-response", required = false)
					final String captchaResponse,
			final Model model) {

		boolean hasCaptcha = true;
		model.addAttribute("hasCaptcha", hasCaptcha);

		boolean hasInput = contrib.hasInput();
		model.addAttribute("hasInput", hasInput);

		if (hasInput) {

			boolean captchaValid = false;
			try {
				captchaValid = captchaService
						.isResponseValid("", captchaResponse);
			} catch (CaptchaException e) {
				LOGGER.error(e.getMessage(), e);
			}
			model.addAttribute("captchaValid", captchaValid);

			if (captchaValid) {

				boolean messageSent = false;
				try {
					mailerService.send(contrib);
					messageSent = true;
				} catch (MailerException e) {
					LOGGER.error(e.getMessage(), e);
				}
				model.addAttribute("messageSent", messageSent);
				contrib.setSpelling(null);
				contrib.setInfl(null);
				contrib.setTrans(null);
				contrib.setComments(null);

			}

		}

		model.addAttribute("contrib", contrib);
		return ViewConstants.CONTRIB_VIEW;

	}

	/**
	 * Controller method for the Downloads page.
	 * @return the name of the Downloads view
	 */
	@RequestMapping(
			value = "/download",
			method = RequestMethod.GET)
	public String download() {
		return ViewConstants.DOWNLOAD_VIEW;
	}

	/**
	 * Controller method for the main page with the search interface.
	 * @param term the term to search for
	 * @param match the way matching should be performed
	 * @param model the model to pass to the view
	 * @return the name of the main view
	 */
	@RequestMapping(
			value = "/",
			method = RequestMethod.GET)
	public String search(
			@RequestParam(value = "term", required = false) final String term,
			@RequestParam(value = "match", required = false) final String match,
			final Model model) {
		if (term != null && !term.isEmpty()) {
			try {
				model.addAttribute("term", term);
				model.addAttribute("match", match);
				model.addAttribute("hu", Language.HU);
				model.addAttribute("nb", Language.NB);
				Map<Language, Response> responses =
						searchService.search(term, match);
				model.addAttribute("responses", responses);
			} catch (SearchException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return ViewConstants.SEARCH_VIEW;
	}

}
