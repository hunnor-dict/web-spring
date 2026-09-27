package net.hunnor.dict.client.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hunnor.dict.client.model.Autocomplete;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.Response;
import net.hunnor.dict.client.service.SearchService;
import net.hunnor.dict.client.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for API endpoints.
 */
@Controller
public class ApiController {

  private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

  private final SearchService searchService;

  public ApiController(SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * Controller method for search suggestions (jQuery).
   *
   * @param term the term to return suggestions for
   * @return search suggestions in JSON
   */
  @GetMapping(value = "/suggest", produces = {"application/json"})
  @ResponseBody
  public List<Autocomplete> suggest(@RequestParam(value = "term", required = false) String term) {
    List<Autocomplete> result = new ArrayList<>();
    try {
      result = searchService.suggest(term);
    } catch (ServiceException ex) {
      logger.error(ex.getMessage(), ex);
    }
    return result;
  }

  /**
   * Controller method for search suggestions (OpenSearch).
   *
   * @param term the term to return suggestions for
   * @return search suggestions in JSON
   */
  @GetMapping(value = "/opensearch/suggest", produces = {"application/x-suggestions+json"})
  @ResponseBody
  public Object[] opensearchSuggest(@RequestParam(value = "term", required = false) String term) {

    Object[] result = new Object[2];

    String sanitizedTerm = term;
    if (sanitizedTerm != null) {
      sanitizedTerm = sanitizedTerm.replaceAll("[^0-9a-zA-ZæøåÆØÅáéíóöőúüűÁÉÍÓÖŐÚÜŰ]", "");
    }
    result[0] = sanitizedTerm;

    List<Autocomplete> autocomplete = null;
    try {
      autocomplete = searchService.suggest(sanitizedTerm);
    } catch (ServiceException ex) {
      logger.error(ex.getMessage(), ex);
    }

    if (autocomplete == null) {
      result[1] = new Object[0];
    } else {
      Object[] suggestions = new Object[autocomplete.size()];
      for (int i = 0; i < autocomplete.size(); i++) {
        suggestions[i] = autocomplete.get(i).getValue();
        result[1] = suggestions;
      }
    }

    return result;

  }

  /**
   * Controller method for search.
   *
   * @param term the term to search for
   * @param match the way matching should be performed
   * @return search results as JSON array of Autocomplete
   */
  @GetMapping(value = "/search", produces = {"application/json"})
  @ResponseBody
  public Map<Language, Response> search(
      @RequestParam(value = "term", required = false) String term,
      @RequestParam(value = "match", required = false) String match) {
    try {
      return searchService.search(term, match);
    } catch (ServiceException ex) {
      logger.error(ex.getMessage(), ex);
      return new HashMap<>();
    }
  }

}
