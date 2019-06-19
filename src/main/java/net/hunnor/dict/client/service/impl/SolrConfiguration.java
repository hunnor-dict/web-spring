package net.hunnor.dict.client.service.impl;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfiguration {

  @Value("${net.hunnor.dict.client.search.solr.url}")
  private String baseUrl;

  @Value("${net.hunnor.dict.client.search.collation.rules}")
  private String collationRules;

  @Bean
  public SolrClient solrClient() {
    return new HttpSolrClient.Builder(baseUrl).build();
  }

  @Bean
  public Collator collator() throws ParseException {
    return new RuleBasedCollator(collationRules);
  }

}
