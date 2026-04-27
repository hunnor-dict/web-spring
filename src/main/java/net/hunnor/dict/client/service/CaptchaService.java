package net.hunnor.dict.client.service;

/**
 * Service interface for verifying CAPTCHA responses.
 */
public interface CaptchaService {

  boolean isResponseValid(String remoteIp, String response) throws ServiceException;

}
