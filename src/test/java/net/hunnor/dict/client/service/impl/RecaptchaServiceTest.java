package net.hunnor.dict.client.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import net.hunnor.dict.client.service.CaptchaService;
import net.hunnor.dict.client.service.ServiceException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecaptchaServiceTest {

  @Autowired
  private CaptchaService captchaService;

  @Autowired
  private RestTemplate restTemplate;

  @Test
  public void testValidation() throws ServiceException {
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(anything()).andRespond(
        withSuccess("{\"success\":true}", MediaType.APPLICATION_JSON_UTF8));
    assertTrue(captchaService.isResponseValid("", ""));
  }

  @Test
  public void testValidationEmptyResponse() throws ServiceException {
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(anything()).andRespond(
        withSuccess("", MediaType.APPLICATION_JSON_UTF8));
    assertFalse(captchaService.isResponseValid("", ""));
  }

  @Test(expected = ServiceException.class)
  public void testValidationError() throws ServiceException {
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(anything()).andRespond(
        withServerError());
    assertFalse(captchaService.isResponseValid("", ""));
  }

}
