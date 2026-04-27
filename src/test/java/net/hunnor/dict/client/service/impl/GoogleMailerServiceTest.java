package net.hunnor.dict.client.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import jakarta.mail.internet.MimeMessage;
import net.hunnor.dict.client.model.Contrib;
import net.hunnor.dict.client.service.MailerService;
import net.hunnor.dict.client.service.ServiceException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class GoogleMailerServiceTest {

  @Autowired
  private MailerService mailerService;

  @MockitoSpyBean
  private JavaMailSender javaMailSender;

  @Test
  void testSend() throws ServiceException {
    assertNotNull(javaMailSender);
    doNothing().when(javaMailSender).send(ArgumentMatchers.any(MimeMessage.class));
    Contrib contrib = new Contrib();
    mailerService.send(contrib);
  }

  @Test
  void testSendError() throws ServiceException {
    assertThrows(ServiceException.class, () -> {
      doThrow(MailSendException.class).when(javaMailSender)
          .send(ArgumentMatchers.any(MimeMessage.class));
      Contrib contrib = new Contrib();
      mailerService.send(contrib);
    });
  }

}
