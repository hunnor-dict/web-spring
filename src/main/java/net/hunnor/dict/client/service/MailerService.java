package net.hunnor.dict.client.service;

import net.hunnor.dict.client.model.Contrib;

public interface MailerService {

  void send(Contrib contrib) throws ServiceException;

}
