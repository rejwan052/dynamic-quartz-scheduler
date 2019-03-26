package com.info.email.services;

import static org.quartz.JobKey.jobKey;
import static org.quartz.impl.matchers.GroupMatcher.anyJobGroup;
import static org.quartz.impl.matchers.GroupMatcher.jobGroupEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.info.email.model.JobDescriptor;

public abstract class AbstractJobService implements JobService {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	protected Scheduler scheduler;

	public AbstractJobService(Scheduler scheduler) {
		super();
		this.scheduler = scheduler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract JobDescriptor createJob(String group, JobDescriptor descriptor);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<JobDescriptor> findJobs() {
		Set<JobDescriptor> descriptors = new HashSet<>();
		try {
			Set<JobKey> keys = scheduler.getJobKeys(anyJobGroup());
			for (JobKey key : keys) {
				JobDetail jobDetail = scheduler.getJobDetail(key);
				descriptors.add(JobDescriptor.buildDescriptor(jobDetail, scheduler.getTriggersOfJob(key)));
			}
		} catch (SchedulerException e) {
			LOGGER.error("Could not find any jobs due to error - {}", e.getLocalizedMessage(), e);
			throw new RuntimeException(e.getLocalizedMessage());
		}
		return descriptors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<JobDescriptor> findGroupJobs(String group) {
		Set<JobDescriptor> descriptors = new HashSet<>();
		try {
			Set<JobKey> keys = scheduler.getJobKeys(jobGroupEquals(group));
			for (JobKey key : keys) {
				JobDetail jobDetail = scheduler.getJobDetail(key);
				descriptors.add(JobDescriptor.buildDescriptor(jobDetail, scheduler.getTriggersOfJob(key)));
			}
		} catch (SchedulerException e) {
			LOGGER.error("Could not find any jobs due to error - {}", e.getLocalizedMessage(), e);
			throw new RuntimeException(e.getLocalizedMessage());
		}
		return descriptors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
	public Optional<JobDescriptor> findJob(String group, String name) {
		// @formatter:off
		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
			if(Objects.nonNull(jobDetail))
				return Optional.of(
						JobDescriptor.buildDescriptor(jobDetail, 
								scheduler.getTriggersOfJob(jobKey(name, group))));
		} catch (SchedulerException e) {
			LOGGER.error("Could not find job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
		// @formatter:on
		LOGGER.warn("Could not find job with key - {}.{}", group, name);
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void updateJob(String group, String name, JobDescriptor descriptor);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteJob(String group, String name) {
		try {
			scheduler.deleteJob(jobKey(name, group));
			LOGGER.info("Deleted job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			LOGGER.error("Could not delete job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllJobs() {
		try {
			Set<JobKey> keys = scheduler.getJobKeys(anyJobGroup());
			scheduler.deleteJobs(new ArrayList<JobKey>(keys));
			LOGGER.info("Deleted all jobs");
		} catch (SchedulerException e) {
			LOGGER.error("Could not delete all jobs with key due to error - {}", e.getLocalizedMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseJob(String group, String name) {
		try {
			scheduler.pauseJob(jobKey(name, group));
			LOGGER.info("Paused job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			LOGGER.error("Could not pause job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resumeJob(String group, String name) {
		try {
			scheduler.resumeJob(jobKey(name, group));
			LOGGER.info("Resumed job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			LOGGER.error("Could not resume job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}
}
