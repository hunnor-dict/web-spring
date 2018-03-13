package net.hunnor.dict.client.service.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import net.hunnor.dict.client.model.Contrib;
import net.hunnor.dict.client.service.MailerService;
import net.hunnor.dict.client.service.ServiceException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoogleMailerServiceTest {

  @Autowired
  private MailerService mailerService;

  @SpyBean
  private JavaMailSender javaMailSender;

  @Test
  public void testSend() throws ServiceException {
    doNothing().when(javaMailSender).send(ArgumentMatchers.any(MimeMessage.class));
    Contrib contrib = new Contrib();
    mailerService.send(contrib);
  }

  @Test(expected = ServiceException.class)
  public void testSendError() throws ServiceException {
    doThrow(MailSendException.class).when(javaMailSender)
        .send(ArgumentMatchers.any(MimeMessage.class));
    Contrib contrib = new Contrib();
    mailerService.send(contrib);
  }

}
