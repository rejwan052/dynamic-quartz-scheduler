package com.info.email.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailJobListener extends JobListenerSupport {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private JavaMailSender mailSender;

	@Override
	public String getName() {
		return "Email Listener";
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		LOGGER.info("Mail is initialized :- {}", mailSender);
	}
}
