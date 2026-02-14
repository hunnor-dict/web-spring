package net.hunnor.dict.client;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  /**
   * Code from a reCAPTCHA tutorial.
   * @param httpRequestFactory parameter
   * @return return value
   */
  @Bean
  public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {
    RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    return restTemplate;
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
