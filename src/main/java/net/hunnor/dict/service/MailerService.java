package net.hunnor.dict.service;

import net.hunnor.dict.model.Contrib;
import net.hunnor.dict.model.MailerException;

/**
 * Service for sending e-mails.
 */
public interface MailerService {

	/**
	 * Send a suggestion in e-mail.
	 * @param contrib the suggestion to send
	 * @throws MailerException if sending the e-mail fails
	 */
	void send(Contrib contrib) throws MailerException;

}
