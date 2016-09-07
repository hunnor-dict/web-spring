package net.hunnor.dict.service.impl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.hunnor.dict.model.CaptchaException;
import net.hunnor.dict.service.CaptchaService;

/**
 * CaptchaService implementation with reCAPTCHA.
 */
@Service
public final class RecaptchaService implements CaptchaService {

	/**
	 * Code from a reCAPTCHA tutorial.
	 */
	private static class RecaptchaResponse {

		/**
		 * Code from a reCAPTCHA tutorial.
		 */
		@JsonProperty("success")
		private boolean success;

		/**
		 * Code from a reCAPTCHA tutorial.
		 */
		@JsonProperty("error-codes")
		private Collection<String> errorCodes;

	}

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(CaptchaService.class);

	/**
	 * The URL to send the validation request to.
	 */
	@Value("${net.hunnor.dict.contrib.recaptcha.url}")
	private String recaptchaUrl;

	/**
	 * The secret key of the reCAPTCHA account.
	 */
	@Value("${net.hunnor.dict.contrib.recaptcha.secret-key}")
	private String recaptchaSecretKey;

	/**
	 * Code from a reCAPTCHA tutorial.
	 */
	private final RestTemplate restTemplate;

	/**
	 * Code from a reCAPTCHA tutorial.
	 * @param template parameter
	 */
	@Autowired
	public RecaptchaService(final RestTemplate template) {
		this.restTemplate = template;
	}

	@Override
	public boolean isResponseValid(
			final String remoteIp,
			final String response) throws CaptchaException {
		RecaptchaResponse recaptchaResponse = null;
		try {
			recaptchaResponse = restTemplate.postForEntity(
                    recaptchaUrl,
                    createBody(recaptchaSecretKey, remoteIp, response),
                    RecaptchaResponse.class)
                    .getBody();
		} catch (RestClientException e) {
			LOGGER.error(e.getMessage(), e);
			throw new CaptchaException();
		}
		return recaptchaResponse.success;
	}

	/**
	 * Code from a reCAPTCHA tutorial.
	 * @param secret the secret key of the reCAPTCHA account
	 * @param remoteIp parameter
	 * @param response parameter
	 * @return return value
	 */
	private MultiValueMap<String, String> createBody(
			final String secret,
			final String remoteIp,
			final String response) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("secret", secret);
		form.add("remoteip", remoteIp);
		form.add("response", response);
		return form;
	}

}
