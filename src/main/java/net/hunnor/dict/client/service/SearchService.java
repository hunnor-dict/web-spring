package net.hunnor.dict.client.service;

import net.hunnor.dict.client.model.Autocomplete;
import net.hunnor.dict.client.model.Language;
import net.hunnor.dict.client.model.Response;

import java.util.List;
import java.util.Map;

public interface SearchService {

  public Map<Language, Long> counts() throws ServiceException;

  public Map<Language, Response> search(String term, String match) throws ServiceException;

  public List<Autocomplete> suggest(String term) throws ServiceException;

}
