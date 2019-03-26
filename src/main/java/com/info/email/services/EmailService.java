package com.info.email.services;


import static org.quartz.JobKey.jobKey;
import static org.quartz.impl.matchers.KeyMatcher.keyEquals;

import java.util.Objects;
import java.util.Set;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.info.email.job.EmailJobListener;
import com.info.email.model.JobDescriptor;


@Service
@Transactional
public class EmailService extends AbstractJobService {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private EmailJobListener jobListener;

	public EmailService(Scheduler scheduler, EmailJobListener jobListener) {
		super(scheduler);
		this.jobListener = jobListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JobDescriptor createJob(String group, JobDescriptor descriptor) {
		String name = descriptor.getName();
		try {
			if (scheduler.checkExists(jobKey(name, group)))
				throw new DataIntegrityViolationException("Job with Key '" + group + "." + name +"' already exists");
			descriptor.setGroup(group);
			JobDetail jobDetail = descriptor.buildJobDetail();
			Set<Trigger> triggersForJob = descriptor.buildTriggers();
			LOGGER.info("About to save job with key - {}", jobDetail.getKey());
			scheduler.scheduleJob(jobDetail, triggersForJob, false);
			LOGGER.info("Job with key - {} saved sucessfully", jobDetail.getKey());
			scheduler.getListenerManager().addJobListener(jobListener, keyEquals(jobDetail.getKey()) );
		} catch (SchedulerException e) {
			LOGGER.error("Could not save job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
		return descriptor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateJob(String group, String name, JobDescriptor descriptor) {
		try {
			JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(name, group));
			if (Objects.nonNull(oldJobDetail)) {
				JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
				jobDataMap.put("subject", descriptor.getSubject());
				jobDataMap.put("messageBody", descriptor.getMessageBody());
				jobDataMap.put("to", descriptor.getTo());
				jobDataMap.put("cc", descriptor.getCc());
				jobDataMap.put("bcc", descriptor.getBcc());
				JobBuilder jb = oldJobDetail.getJobBuilder();
				JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
				scheduler.addJob(newJobDetail, true);
				LOGGER.info("Updated job with key - {}", newJobDetail.getKey());
				return;
			}
			LOGGER.warn("Could not find job with key - {}.{} to update", group, name);
		} catch (SchedulerException e) {
			LOGGER.error("Could not find job with key - {}.{} to update due to error - {}", group, name, e.getLocalizedMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

}
