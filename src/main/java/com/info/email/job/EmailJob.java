package com.info.email.job;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import lombok.Setter;

@Setter
public class EmailJob implements Job {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private JavaMailSender mailSender;
	private String subject;
	private String messageBody;
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("Job triggered to send email to {}", to);
		sendEmail();
		LOGGER.info("Job completed");
	}

	/**
	 * Iterates through the list of email and sends email to recipients
	 */
	private void sendEmail() {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message);
			for (String receipient : to) {
				helper.setFrom("rejwan052@gmail.com", "Dynamic Quartz");
				helper.setTo(receipient);
				helper.setSubject(subject);
				helper.setText(messageBody);
				if (!isEmpty(cc))
					helper.setCc(cc.stream().toArray(String[]::new));
				if (!isEmpty(bcc))
					helper.setBcc(bcc.stream().toArray(String[]::new));
				mailSender.send(message);
			}
		} catch (MessagingException | UnsupportedEncodingException e) {
			LOGGER.error("An error occurred: {}", e.getLocalizedMessage());
		}
	}
}