package net.hunnor.dict;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Code from a reCAPTCHA tutorial.
 */
@Configuration
public class RestTemplateConfig {

	/**
	 * Code from a reCAPTCHA tutorial.
	 * @param httpRequestFactory parameter
	 * @return return value
	 */
	//CHECKSTYLE:OFF
	@Bean
	//CHECKSTYLE:ON
	public RestTemplate restTemplate(
			final ClientHttpRequestFactory httpRequestFactory) {
		RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
		restTemplate
				.getMessageConverters()
				.add(new MappingJackson2HttpMessageConverter());
		return restTemplate;
	}

	/**
	 * Code from a reCAPTCHA tutorial.
	 * @param httpClient parameter
	 * @return return value
	 */
	//CHECKSTYLE:OFF
	@Bean
	//CHECKSTYLE:ON
	public ClientHttpRequestFactory httpRequestFactory(
			final HttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	/**
	 * Code from a reCAPTCHA tutorial.
	 * @return return value
	 */
	//CHECKSTYLE:OFF
	@Bean
	//CHECKSTYLE:ON
	public HttpClient httpClient() {
		return HttpClientBuilder.create().build();
	}

}
