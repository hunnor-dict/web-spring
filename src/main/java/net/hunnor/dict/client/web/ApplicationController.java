package net.hunnor.dict.client.web;

import net.hunnor.dict.client.model.Contrib;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.Response;
import net.hunnor.dict.client.service.CaptchaService;
import net.hunnor.dict.client.service.MailerService;
import net.hunnor.dict.client.service.SearchService;
import net.hunnor.dict.client.service.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class ApplicationController {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

  private static final String ABOUT_VIEW = "views/about/index";

  private static final String CONTRIB_VIEW = "views/contrib/index";

  private static final String COOKIES_VIEW = "views/cookies/index";

  private static final String DOWNLOAD_VIEW = "views/download/index";

  private static final String SEARCH_VIEW = "views/search/index";

  @Autowired
  private CaptchaService captchaService;

  @Autowired
  private MailerService mailerService;

  @Autowired
  private SearchService searchService;

  /**
   * Controller method for the About page.
   * @param model the model to pass to the view
   * @return the name of the About view
   */
  @GetMapping(value = "/about")
  public String about(Model model) {
    try {
      Map<Language, Long> counts = searchService.counts();
      model.addAttribute("hu", counts.get(Language.HU));
      model.addAttribute("nb", counts.get(Language.NB));
    } catch (ServiceException ex) {
      logger.error(ex.getMessage(), ex);
    }
    return ABOUT_VIEW;
  }

  @GetMapping(value = "/contrib")
  public String contribPost(@ModelAttribute Contrib contrib) {
    return CONTRIB_VIEW;
  }

  /**
   * Controller method for the suggestions page.
   * @param contrib the suggestion being processed
   * @param captchaResponse response from the CAPTCHA validation service
   * @param model the model to pass to the view
   * @return the name of the suggestions view
   */
  @PostMapping(value = "/contrib")
  public String contrib(
      @ModelAttribute Contrib contrib,
      @RequestParam(value = "g-recaptcha-response", required = false) String captchaResponse,
      Model model) {
    model.addAttribute("hasCaptcha", true);
    model.addAttribute("hasInput", contrib.hasInput());
    if (contrib.hasInput()) {
      boolean captchaValid = false;
      try {
        captchaValid = captchaService.isResponseValid("", captchaResponse);
      } catch (ServiceException ex) {
        logger.error(ex.getMessage(), ex);
      }
      model.addAttribute("captchaValid", captchaValid);
      if (captchaValid) {
        boolean messageSent = false;
        try {
          mailerService.send(contrib);
        } catch (ServiceException ex) {
          logger.error(ex.getMessage(), ex);
        }
        messageSent = true;
        model.addAttribute("messageSent", messageSent);
        contrib = new Contrib();
      }
    }
    model.addAttribute("contrib", contrib);
    return CONTRIB_VIEW;
  }

  @GetMapping(value = "/cookies")
  public String cookies() {
    return COOKIES_VIEW;
  }

  @GetMapping(value = "/download")
  public String download() {
    return DOWNLOAD_VIEW;
  }

  /**
   * Controller method for the main page with the search interface.
   * @param term the term to search for
   * @param match the way matching should be performed
   * @param model the model to pass to the view
   * @return the name of the main view
   */
  @GetMapping(value = "/")
  public String search(
      @RequestParam(value = "term", required = false) String term,
      @RequestParam(value = "match", required = false) String match,
      @CookieValue(name = "view", required = false) String view,
      Model model) {
    if (term != null && !term.isEmpty()) {
      model.addAttribute("term", term);
      model.addAttribute("match", match);
      model.addAttribute("hu", Language.HU);
      model.addAttribute("nb", Language.NB);
      try {
        Map<Language, Response> responses = searchService.search(term, match);
        model.addAttribute("responses", responses);
      } catch (ServiceException ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
    if (("tree").equals(view)) {
      model.addAttribute("view", "tree");
    }
    return SEARCH_VIEW;
  }

}
