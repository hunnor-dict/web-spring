package net.hunnor.dict.client.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.hunnor.dict.client.service.CaptchaService;
import net.hunnor.dict.client.service.ServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Service
public class RecaptchaService implements CaptchaService {

  private static class RecaptchaResponse {

    @JsonProperty("success")
    public boolean success;

    @JsonProperty("error-codes")
    public Collection<String> errorCodes;

  }

  @Value("${net.hunnor.dict.client.contrib.recaptcha.url}")
  private String recaptchaUrl;

  @Value("${net.hunnor.dict.client.contrib.recaptcha.secret-key}")
  private String recaptchaSecretKey;

  @Autowired
  private RestTemplate restTemplate;

  @Override
  public boolean isResponseValid(String remoteIp, String response) throws ServiceException {
    boolean isValid = false;
    try {
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("secret", recaptchaSecretKey);
      body.add("remoteip", remoteIp);
      body.add("response", response);
      ResponseEntity<RecaptchaResponse> responseEntity = restTemplate.postForEntity(
          recaptchaUrl, body, RecaptchaResponse.class);
      if (responseEntity.hasBody()) {
        RecaptchaResponse recaptchaResponse = responseEntity.getBody();
        // FindBugs false positive NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE
        // See findbugs-exclude.xml
        isValid = recaptchaResponse.success;
      }
    } catch (RestClientException ex) {
      throw new ServiceException(ex.getMessage(), ex);
    }
    return isValid;
  }

}
