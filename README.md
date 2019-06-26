자바 Quartz 오픈 소스 예제
====================================

자바 어플리케이션에서 사용할 수 있는 유명한 오픈 소스 작업 스케쥴링 라이브러리인
Quartz 의 기본 설정 및 사용 방법에 대해 알아보자.

---

### 1. 프로젝트 환경
- Java8
- Quartz 2.3.0
- SLF4J 1.7.7
- Maven 3.3.3
- Eclipse 4.10.0

---

### 2. Quartz 다운로드
- Quartz Team github : _[Quartz GitHub Link](https://github.com/quartz-scheduler/quartz/blob/master/docs/downloads.adoc)_

---

### 3. Maven 의존성 추가

3.1. Quartz.jar

~~~xml
<!-- Quartz Core -->
<dependency>
  <groupId>org.quartz-scheduler</groupId>
  <artifactId>quartz</artifactId>
  <version>latest-x.y.z</version>
</dependency>
~~~

3.2. 선택적 요소

~~~xml
<!-- Quartz uses SLF4J, so we need an actual logger -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.3</version>
</dependency>

<!-- DB Driver if you choose to use PostgreSQL as Quartz JDBCStore -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.5</version>
</dependency>
~~~

---

### 4. Quartz.properties
4-1. 설정 정보 : _[Configuration Link](https://github.com/quartz-scheduler/quartz/blob/master/docs/configuration.adoc)_

4-2. quartz.properties

~~~
org.quartz.scheduler.instanceName = MyScheduler 		 //스케쥴러 이름 설정
org.quartz.threadPool.threadCount = 3 				 //Quartz 쓰레드풀 갯수 설정
org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore	//작업 및 트리거의 세부 정보 설정
~~~  

---

### 5. 샘플 응용 프로그램 시작
> 샘플 응용 프로그램 다운 : _[Quartz Examples Github](https://github.com/ParkHyeokJin/Quartz-Examples.git)_

__5-1. Quartz 인스턴스 생성__<br />
	org.quartz.SchedulerFactory 는 스케쥴러 인스턴스를 생성을 담당하는 인터페이스 입니다. 인터페이스를 구현 하는 모든 클래스는 다음 메소드를 구현 해야 합니다.
    
~~~java
public void createScheduler(){
	SchedulerFactory factory = new StdSchedulerFactory();
	Scheduler scheduler = factory.getScheduler();
}
~~~

---

__5-2. Job Class 생성__<br />
	Job 인터페이스를 참조하는 SimpleJob 클래스는 아래와 같이 execute를 구현 합니다.

~~~java
public class SimpleJob implements Job{
	private final Logger logger = LoggerFactory.getLogger(SimpleJob.class);
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("Simple Job Executed!!!");
	}
}
~~~

---

__5.3. Job 세부 정보 설정__<br />
	org.quartz.JobDetail 을 이용 하여 인스턴스의 세부 특성을 설정 할 수 있다.

~~~java
private JobDetail createJobDetail(String jobName, String jobGroup) {
    return JobBuilder.newJob(SimpleJob.class)
                    .withIdentity(jobName, jobGroup)
                    .build();
}
~~~

---

__5.4. Trigger__<br />
	org.quartz.Trigger는 모든 트리거에 공통 특성을 가진 기본 인터페이스 입니다. 트리거는 작업이 예약 되는 메커니즘이며 많은 트리거가 동일한 작업을 가리킬 수 있지만 단일 트리거는 하나의 작업만 가리킬 수 있습니다.

* Trigger 의 종류
    * SimpleTrigger : 설정된 시간으로 반복되는 트리거
    * CronTrigger : 유닉스 Cron 시간 타임으로 설정 되는 트리거
  
1-1) SimpleTrigger 예제
~~~java
private SimpleTrigger createTrigger(String triggerName, String triggerGroup, int intervalTime) {
    return TriggerBuilder.newTrigger()
                        .withIdentity(triggerName, triggerGroup)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(intervalTime)
                            .repeatForever())
                        .build();
}
~~~

1-2) CronTrigger 예제
~~~java
private CronTrigger createCronTrigger(String triggerName, String triggerGroup){
    CronExpression cronExpression = new CronExpression("* * * * * ?");
    return TriggerBuilder.newTrigger()
            .withIdentity(triggerName, triggerGroup)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .build();
}
~~~

---

__5.5. Job 예약하기__
~~~java
scheduler.scheduleJob(job, trigger);
~~~

---

__5.6. Job 시작하기__
~~~java
scheduler.start();
~~~

---

__5.7. Job 종료 하기__

* _boolean waitForJobsToComplete 인수로 모든 작업이 완료 될 때까지 스케쥴러의 종료를 대기 할 수 있습니다._  

~~~java
scheduler.shutdown(boolean waitForJobsToComplete);
~~~


---

### 6. Quartz 선택적 요소
__6.1. JobStore__<br />
org.quartz.spi.JobStore 인터페이스는 QuartzScheduler을 사용 하기 위해 Job과 Trigger 스토리지 메커니즘을
제공하고자 하는 클래스에 의해 구현 된다. JobStore 인터페이스는 두 가지의 구현이 있다.
  
1) RAMJobStore : RAMJobStore는 JobStore의 저장장소로 RAM을 이용 합니다. 액세스에는 매우 빠르지만 휘발성이므로
                지속성이 필요한 경우에는 사용 하지 말아햐 합니다.  
2) JobStoreSupport : JobStoreSupport는 JDBC 기반의 기본 기능을 포함한 JobStore를 구현 합니다.

- JobStoreSupport quartz.properties
~~~
org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
  org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
  org.quartz.jobStore.dataSource=quartzDataSource
  org.quartz.jobStore.tablePrefix=QRTZ_
  org.quartz.threadPool.class=org.quartz.simpl.SimpleThreadPool
  org.quartz.dataSource.quartzDataSource.driver=com.mysql.jdbc.Driver
  org.quartz.dataSource.quartzDataSource.URL=jdbc:mysql://localhost:3306/quartz_schema
  org.quartz.dataSource.quartzDataSource.user=user
  org.quartz.dataSource.quartzDataSource.password=password
~~~

__6.2. JobListener__<br />
org.quartz.JobListener 클래스는 JobDetail이 실행될 때 통보 받기를 원하는 클래스에 의해 구현되는
인터페이스 입니다.

- MyJobListener.java

~~~java
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
~~~

- JobListener의 실행 순서 : MyJobListener.jobToBeExecuted() -> MyJob.execute() -> MyJobListener.jobWasExecuted()

- MyJobListener을 스케줄러에 등록
~~~java
scheduler.getListenerManager().addJobListener(new MyJobListener());
~~~

__6-3. TriggerListener__<br />

JobListener과 마찬가지로 org.quartz.TriggerListener은 트리거가 실행 될 때 알림을 받고 싶은
클래스에 의해 구현되는 인터페이스 입니다.

- MyTriggerListener.java
    
~~~java
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
~~~

- TriggerListener의 실행순서 : MyTriggerListener.triggerFired() -> MyJob.execute() -> MyJobListener.triggerComplete()

- MyTriggerListener 스케줄러에 등록
~~~java
scheduler.getListenerManager().addTriggerListener(new MyTriggerListener.class);
~~~

이처럼 Quarts 를 이용 하여 반복적으로 작동되는 스케줄러를 생성 할 수 있으며
실전에 사용 된 Quarts 소스는 다음 글에서 정리 해보도록 하겠다.