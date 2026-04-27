package net.hunnor.dict.client.service;

import net.hunnor.dict.client.model.Contrib;

/**
 * Service interface for sending emails with contributions.
 */
public interface MailerService {

  void send(Contrib contrib) throws ServiceException;

}
