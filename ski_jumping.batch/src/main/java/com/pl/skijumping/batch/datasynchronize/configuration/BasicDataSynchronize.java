package com.pl.skijumping.batch.datasynchronize.configuration;

import com.pl.skijumping.batch.datareaderjob.jobs.findracedata.processor.FindRaceDataProcessorBatch;
import com.pl.skijumping.batch.datareaderjob.jobs.findracedata.writer.FindRaceDataWriterBatch;
import com.pl.skijumping.batch.datasynchronize.BasicDataSynchronizeTasklet;
import com.pl.skijumping.batch.datasynchronize.reader.BasicDataSynchronizeReaderBatch;
import com.pl.skijumping.batch.listener.StepListener;
import com.pl.skijumping.client.IHtmlDownloader;
import com.pl.skijumping.diagnosticmonitor.DiagnosticMonitor;
import com.pl.skijumping.dto.DataRaceDTO;
import com.pl.skijumping.service.CompetitionNameService;
import com.pl.skijumping.service.CompetitionTypeService;
import com.pl.skijumping.service.DataRaceService;
import com.pl.skijumping.service.TournamentYearService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BasicDataSynchronize {
    public static final String BASIC_DATA_SYNCHRONIZE_JOB_NAME = "Basic_Data_Synchronize_Job";
    public static final String BASIC_DATA_IMPORTER_STEP_NAME = "Basic_Data_Importer_Step";
    public static final String BASIC_DATA_SYNCHRONIZE_STEP_NAME = "Basic_Data_Synchronize_Step";

    private final String host;
    private final String directory;
    private final JobBuilderFactory jobBuilder;
    private final StepBuilderFactory stepBuilder;
    private final DiagnosticMonitor diagnosticMonitor;
    private final TournamentYearService tournamentYearService;
    private final IHtmlDownloader htmlDownloader;
    private final CompetitionTypeService competitionTypeService;
    private final CompetitionNameService competitionNameService;
    private final DataRaceService dataRaceService;

    public BasicDataSynchronize(@Value("${skijumping.settings.hostWithFilterForYear}") String host,
                                @Value("${skijumping.settings.directory}") String directory,
                                IHtmlDownloader htmlDownloader,
                                TournamentYearService tournamentYearService,
                                JobBuilderFactory jobBuilder,
                                StepBuilderFactory stepBuilder,
                                DiagnosticMonitor diagnosticMonitor,
                                CompetitionTypeService competitionTypeService,
                                CompetitionNameService competitionNameService,
                                DataRaceService dataRaceService) {
        this.host = host;
        this.directory = directory;
        this.tournamentYearService = tournamentYearService;
        this.jobBuilder = jobBuilder;
        this.stepBuilder = stepBuilder;
        this.htmlDownloader = htmlDownloader;
        this.diagnosticMonitor = diagnosticMonitor;
        this.competitionNameService = competitionNameService;
        this.competitionTypeService = competitionTypeService;
        this.dataRaceService = dataRaceService;
    }

    @Bean(name = BASIC_DATA_SYNCHRONIZE_JOB_NAME)
    public Job basicDataSynchronizeJob() {
        return this.jobBuilder.get(BASIC_DATA_SYNCHRONIZE_JOB_NAME)
                .start(basicDataImporterStep())
                .next(basicDataSynchronizeStep())
                .build();
    }

    @Bean(name = BASIC_DATA_IMPORTER_STEP_NAME)
    public Step basicDataImporterStep() {
        return this.stepBuilder.get(BASIC_DATA_IMPORTER_STEP_NAME)
                .tasklet(basicDataImporterTasklet())
                .listener(stepExecutionListener())
                .build();
    }

    @Bean(name = BASIC_DATA_SYNCHRONIZE_STEP_NAME)
    public Step basicDataSynchronizeStep() {
        return this.stepBuilder.get(BASIC_DATA_SYNCHRONIZE_STEP_NAME)
                .<String, List<DataRaceDTO>>chunk(1)
                .reader(basicDataSynchronizeReader())
                .processor(basicDataSynchronizeProcessor())
                .writer(findRaceDataWriterBatch())
                .build();
    }

    @Bean
    public ItemStreamReader<String> basicDataSynchronizeReader() {
        return new BasicDataSynchronizeReaderBatch(diagnosticMonitor, directory);
    }

    @Bean
    public ItemProcessor<String, List<DataRaceDTO>> basicDataSynchronizeProcessor() {
        return new FindRaceDataProcessorBatch(diagnosticMonitor);
    }

    @Bean
    public FindRaceDataWriterBatch findRaceDataWriterBatch() {
        return new FindRaceDataWriterBatch(
                competitionTypeService, competitionNameService, dataRaceService, diagnosticMonitor);
    }

    @Bean
    public StepExecutionListener stepListener() {
        return new StepListener();
    }

    @Bean
    public Tasklet basicDataImporterTasklet() {
        return new BasicDataSynchronizeTasklet(host, directory, tournamentYearService, htmlDownloader, diagnosticMonitor);
    }

    @Bean
    public StepExecutionListener stepExecutionListener() {
        return new StepListener();
    }
}

