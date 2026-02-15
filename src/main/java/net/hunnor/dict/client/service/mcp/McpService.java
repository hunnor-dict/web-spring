package net.hunnor.dict.client.service.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hunnor.dict.client.service.SearchService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class McpService {

  @Autowired
  private SearchService searchService;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * MCP tool method for looking up a word in the HunNor dictionary.
   * @param word the word to look up
   * @return a JSON string containing the search results, or an error message if the lookup fails
   */
  @McpTool(description = "Look up a Hungarian or Norwegian word in the HunNor dictionary")
  public String search(
      @McpToolParam(description = "Word to look up", required = true) String word) {
    try {
      var result = searchService.search(word, "definitions");
      return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
      return String.format("Error looking up word '%s': %s", word, e.getMessage());
    }
  }

}
