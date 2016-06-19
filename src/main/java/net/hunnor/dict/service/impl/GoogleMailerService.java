package net.hunnor.dict.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import net.hunnor.dict.model.Contrib;
import net.hunnor.dict.model.MailerException;
import net.hunnor.dict.service.MailerService;

/**
 * MailerService implementation with GMail.
 */
@Service
public class GoogleMailerService implements MailerService {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(MailerService.class);

	/**
	 * The sender of the e-mail.
	 */
	@Value("${net.hunnor.dict.contrib.mail.from}")
	private String mailFrom;

	/**
	 * The address to send the e-mail to.
	 */
	@Value("${net.hunnor.dict.contrib.mail.to}")
	private String mailTo;

	/**
	 * The Reply-To address in the e-mail header.
	 */
	@Value("${net.hunnor.dict.contrib.mail.reply-to}")
	private String replyTo;

	/**
	 * MailSender from spring-boot-starter-mail.
	 */
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public final void send(final Contrib contrib) throws MailerException {

		try {

			MimeMessage message = javaMailSender.createMimeMessage();

			message.setFrom(new InternetAddress(mailFrom));
			message.setRecipient(RecipientType.TO, new InternetAddress(mailTo));
			message.setReplyTo(new InternetAddress[] {
					new InternetAddress(replyTo)});
			message.setSubject(
					"HunNor javaslat: " + contrib.getSpelling(), "UTF-8");

			StringBuilder sb = new StringBuilder();
			sb.append("Javaslat:\n");
			sb.append("-> helyesírás: ").append(contrib.getSpelling())
					.append("\n");
			sb.append("-> szófaj: ").append(contrib.getPos())
					.append("\n");
			sb.append("-> ragozás: ").append(contrib.getInfl())
					.append("\n");
			sb.append("-> fordítás: ").append(contrib.getTrans())
					.append("\n");
			sb.append("-> megjegyzés: ").append(contrib.getComments())
					.append("\n");
			message.setText(sb.toString());

			javaMailSender.send(message);

		} catch (AddressException e) {
			LOGGER.error(e.getMessage(), e);
			throw new MailerException();
		} catch (MessagingException e) {
			LOGGER.error(e.getMessage(), e);
			throw new MailerException();
		}

	}

}
