package com.pl.skijumping.batch.dataimportjob.scheduler;

import com.pl.skijumping.common.util.JobRunner;
import com.pl.skijumping.common.exception.InternalServiceException;
import com.pl.skijumping.diagnosticmonitor.DiagnosticMonitor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.pl.skijumping.batch.dataimportjob.configuration.DataImporterConfiguration.DATA_IMPORT_JOB_NAME;

@Component
public class DataImportScheduler {

    private final JobRunner jobRunner;

    public DataImportScheduler(JobLauncher jobLauncher,
                               @Qualifier(DATA_IMPORT_JOB_NAME) Job dataImportJob,
                               @Value("${skijumping.settings.scheduler.importData.enable}") Boolean isEnable,
                               DiagnosticMonitor diagnosticMonitor) {
        jobRunner = new JobRunner(isEnable, diagnosticMonitor, jobLauncher, dataImportJob, DATA_IMPORT_JOB_NAME);
    }

    @Scheduled(cron = "${skijumping.settings.scheduler.importData.cron}")
    public void importData() throws InternalServiceException {
        jobRunner.run();
    }

    public void addJobParameter(String key, String value) {
        jobRunner.addJobParameter(key, value);
    }
}
