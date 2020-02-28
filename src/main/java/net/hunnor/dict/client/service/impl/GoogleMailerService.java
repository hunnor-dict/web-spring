package net.hunnor.dict.client.service.impl;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import net.hunnor.dict.client.model.Contrib;
import net.hunnor.dict.client.service.MailerService;
import net.hunnor.dict.client.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class GoogleMailerService implements MailerService {

  @Value("${net.hunnor.dict.client.contrib.mail.from}")
  private String mailFrom;

  @Value("${net.hunnor.dict.client.contrib.mail.to}")
  private String mailTo;

  @Value("${net.hunnor.dict.client.contrib.mail.reply-to}")
  private String replyTo;

  @Value("${net.hunnor.dict.client.contrib.mail.subject}")
  private String subject;

  @Value("${spring.mail.default-encoding}")
  private String encoding;

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private TemplateEngine templateEngine;

  @Override
  public void send(Contrib contrib) throws ServiceException {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      message.setFrom(new InternetAddress(mailFrom));
      message.setRecipient(RecipientType.TO, new InternetAddress(mailTo));
      message.setReplyTo(new InternetAddress[] {new InternetAddress(replyTo)});
      message.setSubject(String.format(subject, contrib.getSpelling()), encoding);
      message.setText(buildMessage(contrib));
      javaMailSender.send(message);
    } catch (MailException | MessagingException ex) {
      throw new ServiceException(ex.getMessage(), ex);
    }
  }

  private String buildMessage(Contrib contrib) {
    Context context = new Context();
    context.setVariable("contrib", contrib);
    return templateEngine.process("mail/contrib", context);
  }

}
