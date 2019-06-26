package com.quarts.examples.Listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJobListener implements JobListener{
	private final Logger log = LoggerFactory.getLogger(MyJobListener.class);
	
	public String getName() {
		return MyJobListener.class.getName();
	}

	public void jobToBeExecuted(JobExecutionContext context) {
		log.info("{} is about to be executed", context.getJobDetail().getKey().toString());
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		log.info("{} finised execution", context.getJobDetail().getKey().toString());
	}

	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		log.info("{} was about to be executed but a JobListener vetoed it's execution", context.getJobDetail().getKey().toString());
	}
}
