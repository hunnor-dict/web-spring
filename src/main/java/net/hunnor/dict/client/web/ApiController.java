package net.hunnor.dict.client.web;

import java.util.ArrayList;
import java.util.List;

import net.hunnor.dict.client.model.Autocomplete;
import net.hunnor.dict.client.service.SearchService;
import net.hunnor.dict.client.service.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {

  private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

  @Autowired
  private SearchService searchService;

  /**
   * Controller method for search suggestions (jQuery).
   * @param term term the term to return suggestions for
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
   * Controller method for search suggestion (OpenSearch).
   * @param term the term to return suggestions for
   * @return search suggestions in JSON
   */
  @GetMapping(value = "/opensearch/suggest", produces = {"application/x-suggestions+json"})
  @ResponseBody
  public Object[] opensearchSuggest(@RequestParam(value = "term", required = false) String term) {

    Object[] result = new Object[2];
    result[0] = term;

    List<Autocomplete> autocomplete = null;
    try {
      autocomplete = searchService.suggest(term);
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

}
