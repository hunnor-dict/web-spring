package net.hunnor.dict.service;

import net.hunnor.dict.model.CaptchaException;

/**
 * Service for processing CAPTCHA requests.
 */
public interface CaptchaService {

	/**
	 * Validate the CAPTCHA response.
	 * @param remoteIp TODO
	 * @param response TODO
	 * @return true if the CAPTCHA response is valid, false otherwise
	 * @throws CaptchaException if validation fails
	 */
	boolean isResponseValid(String remoteIp, String response)
			throws CaptchaException;

}
