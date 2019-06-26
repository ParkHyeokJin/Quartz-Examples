package com.quarts.examples.Listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.TriggerListener;

public class MyTriggerListener implements TriggerListener{

	private final Logger log = LoggerFactory.getLogger(MyTriggerListener.class);
	
	public String getName() {
		return MyTriggerListener.class.getName();
	}

	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		log.info("{} trigger is fired", getName());
	}

	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		log.info("{} was about to be executed but a TriggerListener vetoed it's execution", context.getJobDetail().getKey().toString());
        return false;
	}

	public void triggerMisfired(Trigger trigger) {
		log.info("{} trigger was misfired", getName());
	}

	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		log.info("{} trigger is complete", getName());
	}
}
