package net.hunnor.dict.client;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate configuration. Code from a reCAPTCHA tutorial.
 */
@Configuration
public class RestTemplateConfig {

  /**
   * Code from a reCAPTCHA tutorial.
   *
   * @param httpRequestFactory parameter
   * @return return value
   */
  @Bean
  public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {
    return new RestTemplate(httpRequestFactory);
  }

  @Bean
  public ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
    return new HttpComponentsClientHttpRequestFactory(httpClient);
  }

  @Bean
  public HttpClient httpClient() {
    return HttpClientBuilder.create().build();
  }

}
