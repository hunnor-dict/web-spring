package net.hunnor.dict.client.service;

public interface CaptchaService {

  boolean isResponseValid(String remoteIp, String response) throws ServiceException;

}
