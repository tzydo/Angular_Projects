package com.pl.skijumping.batch.jumpresultsynchronize;

import com.pl.skijumping.batch.jumpresultsynchronize.configuration.JumpResultSynchronize;
import com.pl.skijumping.batch.util.JobRunner;
import com.pl.skijumping.common.exception.InternalServiceException;
import com.pl.skijumping.diagnosticmonitor.DiagnosticMonitor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class JumpResultSynchronizeScheduler {
    private final JobLauncher jobLauncher;
    private final Job resultSynchronizeJob;
    private Boolean isEnable;
    private final DiagnosticMonitor diagnosticMonitor;

    public JumpResultSynchronizeScheduler(JobLauncher jobLauncher,
                                          @Qualifier(JumpResultSynchronize.JUMP_RESULT_SYNCHRONIZE_JOB_NAME) Job resultSynchronizeJob,
                                          @Value("${skijumping.settings.scheduler.jumpResultSynchronize.enable}") Boolean isEnable,
                                          DiagnosticMonitor diagnosticMonitor) {
        this.jobLauncher = jobLauncher;
        this.resultSynchronizeJob = resultSynchronizeJob;
        this.isEnable = isEnable;
        this.diagnosticMonitor = diagnosticMonitor;
    }

    @Scheduled(cron = "${skijumping.settings.scheduler.jumpResultSynchronize.cron}")
    public JobExecution synchronizeData() throws InternalServiceException {
        JobRunner jobRunner = new JobRunner(
                isEnable, diagnosticMonitor, jobLauncher, resultSynchronizeJob, JumpResultSynchronize.JUMP_RESULT_SYNCHRONIZE_JOB_NAME);
        return jobRunner.run();
    }
}