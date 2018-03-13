package net.hunnor.dict.client.service.impl;

import net.hunnor.dict.client.model.Contrib;
import net.hunnor.dict.client.service.MailerService;
import net.hunnor.dict.client.service.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class GoogleMailerService implements MailerService {

  private static final Logger logger = LoggerFactory.getLogger(MailerService.class);

  @Value("${net.hunnor.dict.client.contrib.mail.from}")
  private String mailFrom;

  @Value("${net.hunnor.dict.client.contrib.mail.to}")
  private String mailTo;

  @Value("${net.hunnor.dict.client.contrib.mail.reply-to}")
  private String replyTo;

  @Autowired
  private JavaMailSender javaMailSender;

  @Override
  public void send(Contrib contrib) throws ServiceException {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      message.setFrom(new InternetAddress(mailFrom));
      message.setRecipient(RecipientType.TO, new InternetAddress(mailTo));
      message.setReplyTo(new InternetAddress[] {new InternetAddress(replyTo)});
      message.setSubject("HunNor javaslat: " + contrib.getSpelling(), "UTF-8");
      message.setText(buildMessage(contrib));
      javaMailSender.send(message);
    } catch (MailException | MessagingException ex) {
      logger.error(ex.getMessage(), ex);
      throw new ServiceException();
    }
  }

  private String buildMessage(Contrib contrib) {
    StringBuilder sb = new StringBuilder();
    sb.append("-> helyesírás: ").append(contrib.getSpelling()).append("\n");
    sb.append("-> ragozás: ").append(contrib.getInfl()).append("\n");
    sb.append("-> fordítás: ").append(contrib.getTrans()).append("\n");
    sb.append("-> megjegyzés: ").append(contrib.getComments()).append("\n");
    return sb.toString();
  }

}
