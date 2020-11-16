package net.hunnor.dict.client.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import net.hunnor.dict.client.service.CaptchaService;
import net.hunnor.dict.client.service.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class RecaptchaServiceTest {

  @Autowired
  private CaptchaService captchaService;

  @Autowired
  private RestTemplate restTemplate;

  @Test
  public void testValidationTrue() throws ServiceException {
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(anything()).andRespond(
        withSuccess("{\"success\":true}", MediaType.APPLICATION_JSON));
    assertTrue(captchaService.isResponseValid("", ""));
  }

  @Test
  public void testValidationFalse() throws ServiceException {
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(anything()).andRespond(
        withSuccess("{\"success\":false}", MediaType.APPLICATION_JSON));
    assertFalse(captchaService.isResponseValid("", ""));
  }

  @Test
  public void testValidationEmptyResponse() throws ServiceException {
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(anything()).andRespond(
        withSuccess("", MediaType.APPLICATION_JSON));
    assertFalse(captchaService.isResponseValid("", ""));
  }

  @Test
  public void testValidationError() throws ServiceException {
    assertThrows(ServiceException.class, () -> {
      MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
      mockServer.expect(anything()).andRespond(
          withServerError());
      assertFalse(captchaService.isResponseValid("", ""));
    });
  }

}
