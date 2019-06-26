package com.quarts.examples;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quarts.examples.Listener.MyJobListener;
import com.quarts.examples.Listener.MyTriggerListener;
import com.quarts.examples.job.SimpleJob;

public class SimpleJobTest{
	Logger logger = LoggerFactory.getLogger(SimpleJobTest.class);
	private Scheduler scheduler;
	
	@Before
	public void createScheduler() throws SchedulerException {
		SchedulerFactory factory = new StdSchedulerFactory();
		scheduler = factory.getScheduler();
	}
	
	@Test
	public void simpleTriggerTest() throws SchedulerException {
		JobDetail job = createJobDetail("myJob", "myGroup");
		Trigger trigger = createSimpleTrigger("mySimpleTrigger", "myGroup", 1);
		
		putSchduleJob(job, trigger);
		//스케쥴러 시작
		start();
		//Test Sleep
		testSleep(1000 * 5);
		//스케쥴러 종료
		stop(true);
	}
	
	@Test
	public void cronTriggerTest() throws SchedulerException, ParseException {
		JobDetail job = createJobDetail("myJob", "myGroup");
		Trigger trigger = createCronTrigger("mySimpleTrigger", "myGroup", "* * * * * ?");
		
		putSchduleJob(job, trigger);
		//스케쥴러 시작
		start();
		//Test Sleep
		testSleep(1000 * 5);
		//스케쥴러 종료
		stop(true);
	}

	@Test
	public void jobListenerTest() throws SchedulerException {
		scheduler.getListenerManager().addJobListener(new MyJobListener());
		simpleTriggerTest();
	}
	
	@Test
	public void triggerListenerTest() throws SchedulerException {
		scheduler.getListenerManager().addTriggerListener(new MyTriggerListener());
		simpleTriggerTest();
	}
	
	private void stop(boolean waitForJobsToComplete) throws SchedulerException {
		scheduler.shutdown(waitForJobsToComplete);
	}

	private void start() throws SchedulerException {
		scheduler.start();
	}

	private void putSchduleJob(JobDetail job, Trigger trigger) throws SchedulerException {
		scheduler.scheduleJob(job, trigger);
	}

	private SimpleTrigger createSimpleTrigger(String triggerName, String triggerGroup, int intervalTime) {
		return TriggerBuilder.newTrigger()
							.withIdentity(triggerName, triggerGroup)
							.withSchedule(SimpleScheduleBuilder.simpleSchedule()
								.withIntervalInSeconds(intervalTime)
								.repeatForever())
							.build();
	}
	
	private CronTrigger createCronTrigger(String triggerName, String triggerGroup, String cronTime) throws ParseException{
		CronExpression cronExpression = new CronExpression(cronTime);
		return TriggerBuilder.newTrigger()
				.withIdentity(triggerName, triggerGroup)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
	}

	private JobDetail createJobDetail(String jobName, String jobGroup) {
		return JobBuilder.newJob(SimpleJob.class)
						.withIdentity(jobName, jobGroup)
						.build();
	}
	
	private void testSleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
