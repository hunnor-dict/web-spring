package net.hunnor.dict.client.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Redirect URLs used by previous versions to the front page.
 */
@Controller
public class RedirectController {

	/**
	 * Controller method for redirection.
	 * @return a redirect to the front page.
	 */
	@RequestMapping(
			value = {
					"/m",
					"/m/*",
					"/no",
					"/no/*",
					"/search",
					"/gramm",
					"/gramm/*"},
			method = RequestMethod.GET)
	public final String redirect() {
		return ViewConstants.REDIRECT;
	}

}
