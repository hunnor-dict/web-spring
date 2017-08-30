package net.hunnor.dict.client.service;

import net.hunnor.dict.client.model.Contrib;
import net.hunnor.dict.client.model.MailerException;

/**
 * Service for sending e-mails.
 */
@FunctionalInterface
public interface MailerService {

	/**
	 * Send a suggestion in e-mail.
	 * @param contrib the suggestion to send
	 * @throws MailerException if sending the e-mail fails
	 */
	void send(Contrib contrib) throws MailerException;

}
